package org.kpmp.packages;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.bson.Document;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kpmp.logging.LoggingService;
import org.kpmp.users.User;
import org.kpmp.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBRef;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Component
public class CustomPackageRepository {

	private static final String PACKAGES_COLLECTION = "packages";
	private static final String USERS_COLLECTION = "users";

	private static final MessageFormat fileUploadStartTiming = new MessageFormat("Timing|start|{0}|{1}|{2}|{3} files");

	private PackageRepository repo;
	private MongoTemplate mongoTemplate;
	private UniversalIdGenerator universalIdGenerator;
	private UserRepository userRepository;
	private JsonWriterSettingsConstructor jsonSettings;
	private LoggingService logger;
    private StudyFileInfoRepository studyFileInfoRepository;

	@Autowired
	public CustomPackageRepository(PackageRepository repo, MongoTemplate mongoTemplate,
			UniversalIdGenerator universalIdGenerator, UserRepository userRepo,
			JsonWriterSettingsConstructor jsonSettings, StudyFileInfoRepository studyFileInfoRepository, LoggingService logger) {
		this.repo = repo;
		this.mongoTemplate = mongoTemplate;
		this.universalIdGenerator = universalIdGenerator;
		this.userRepository = userRepo;
		this.jsonSettings = jsonSettings;
        this.studyFileInfoRepository = studyFileInfoRepository;
		this.logger = logger;
	}

	public String saveDynamicForm(JSONObject packageMetadata, User userFromHeader, String packageId)
			throws JSONException {
		Date startTime = new Date();
		String site = "";
		String submitterEmail = userFromHeader.getEmail();
		JSONArray files = packageMetadata.getJSONArray(PackageKeys.FILES.getKey());

		logger.logInfoMessage(this.getClass(), userFromHeader, packageId,
				this.getClass().getSimpleName() + ".saveDynamicForm",
				fileUploadStartTiming.format(new Object[] { startTime, submitterEmail, packageId, files.length() }));

		String studyName = packageMetadata.getString(PackageKeys.STUDY.getKey());
		StudyFileInfo studyFileInfo = studyFileInfoRepository.findByStudy(studyName);
		String biopsyId = packageMetadata.getString(PackageKeys.BIOPSY_ID.getKey());
	
		for (int i = 0; i < files.length(); i++) {
			JSONObject file = files.getJSONObject(i);
			file.put(PackageKeys.ID.getKey(), universalIdGenerator.generateUniversalId());
			if (studyFileInfo.getShouldRename()) {
				String originalFileName = file.getString(PackageKeys.FILE_NAME.getKey());
				String fileExtension = FilenameUtils.getExtension(originalFileName);
				String fileRename = biopsyId+"_"+studyFileInfo.getUploadSourceLetter()+"_"+studyFileInfo.getFileCounter()+"."+fileExtension;
				file.put(PackageKeys.ORIGINAL_FILE_NAME.getKey(), originalFileName);
				file.put(PackageKeys.FILE_NAME.getKey(), fileRename);
			}
		}

		User user = findUser(userFromHeader);
		cleanUpObject(packageMetadata);

		DBRef userRef = new DBRef(USERS_COLLECTION, new ObjectId(user.getId()));
		String jsonString = packageMetadata.toString();

		Document document = Document.parse(jsonString);
		document.put(PackageKeys.SUBMITTER.getKey(), userRef);
		document.put(PackageKeys.CREATED_AT.getKey(), startTime);
		document.put(PackageKeys.ID.getKey(), packageId);

		if (document.containsKey(PackageKeys.CUREGN_DIABETES_SITE.getKey())) {
			site = document.getString(PackageKeys.CUREGN_DIABETES_SITE.getKey());
			document.remove(PackageKeys.CUREGN_DIABETES_SITE.getKey());
		} else if (document.containsKey(PackageKeys.CUREGN_SITE.getKey())) {
			site =  document.getString(PackageKeys.CUREGN_SITE.getKey());
			document.remove(PackageKeys.CUREGN_SITE.getKey());
		} else if (document.containsKey(PackageKeys.NEPTUNE_SITE.getKey())) {
			site =  document.getString(PackageKeys.NEPTUNE_SITE.getKey());
			document.remove(PackageKeys.NEPTUNE_SITE.getKey());
		}
		document.put(PackageKeys.SITE.getKey(), site);

		MongoCollection<Document> collection = mongoTemplate.getCollection(PACKAGES_COLLECTION);
		collection.insertOne(document);

		return packageId;
	}

	private void cleanUpObject(JSONObject packageMetadata) {
		packageMetadata.remove(PackageKeys.SUBMITTER.getKey());
		packageMetadata.remove(PackageKeys.SUBMITTER_EMAIL.getKey());
		packageMetadata.remove(PackageKeys.SUBMITTER_FIRST_NAME.getKey());
		packageMetadata.remove(PackageKeys.SUBMITTER_LAST_NAME.getKey());
	}

	private User findUser(User userFromHeader) throws JSONException {
		User user = userRepository.findByEmail(userFromHeader.getEmail());
		if (user == null) {
			User newUser = userFromHeader;
			user = userRepository.save(newUser);
		}
		return user;
	}

	public void updateField(String id, String fieldName, Object value) {
		Query updateQuery = new Query(Criteria.where(PackageKeys.ID.getKey()).is(id));
		Update fieldUpdate = new Update();
		fieldUpdate.set(fieldName, value);
		mongoTemplate.updateFirst(updateQuery, fieldUpdate, PACKAGES_COLLECTION);
	}

	@Cacheable(value = "packages")
	public List<JSONObject> findAll() throws JSONException, JsonProcessingException {
		Query query = new Query();
		query = query.with(Sort.by(Sort.Direction.DESC, PackageKeys.CREATED_AT.getKey()));

		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry());
		DocumentCodec codec = new DocumentCodec(codecRegistry, new BsonTypeClassMap());

		List<Document> results = mongoTemplate.find(query, Document.class, PACKAGES_COLLECTION);
		List<JSONObject> jsons = new ArrayList<>();
		for (Document document : results) {
			JsonWriterSettings settings = jsonSettings.getSettings();
			String json = document.toJson(settings, codec);
			JSONObject jsonObject = setUserInformation(json);
			jsons.add(jsonObject);
		}

		return jsons;
	}

	public JSONObject findOne(String packageId) throws JSONException {
		BasicDBObject query = new BasicDBObject();
		query.put(PackageKeys.ID.getKey(), packageId);

		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry());
		DocumentCodec codec = new DocumentCodec(codecRegistry, new BsonTypeClassMap());

		MongoDatabase db = mongoTemplate.getDb();
		MongoCollection<Document> collection = db.getCollection(PACKAGES_COLLECTION);

		Document document = collection.find(query).first();
		JsonWriterSettings settings = jsonSettings.getSettings();
		String json = document.toJson(settings, codec);
		JSONObject jsonObject = new JSONObject(json);
		return jsonObject;
	}

	public String getJSONByPackageId(String packageId) throws JSONException, JsonProcessingException {

		BasicDBObject query = new BasicDBObject();
		query.put(PackageKeys.ID.getKey(), packageId);

		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry());
		DocumentCodec codec = new DocumentCodec(codecRegistry, new BsonTypeClassMap());
		MongoDatabase db = mongoTemplate.getDb();
		MongoCollection<Document> collection = db.getCollection(PACKAGES_COLLECTION);

		Document document = collection.find(query).first();
		JsonWriterSettings settings = jsonSettings.getSettings();
		String json = document.toJson(settings, codec);

		JSONObject jsonObject = setUserInformation(json);
		jsonObject.remove(PackageKeys.LARGE_FILES_CHECKED.getKey());
		return StringEscapeUtils.unescapeJava(jsonObject.toString());
	}

	private JSONObject setUserInformation(String json) throws JSONException, JsonProcessingException {
		JSONObject jsonObject = new JSONObject(json);
		JSONObject submitter = (JSONObject) jsonObject.get(PackageKeys.SUBMITTER.getKey());
		JSONObject submitterIdObject = (JSONObject) submitter.get(PackageKeys.SUBMITTER_ID_OBJECT.getKey());
		String submitterId = submitterIdObject.getString(PackageKeys.SUBMITTER_ID.getKey());
		Optional<User> userOptional = userRepository.findById(submitterId);
		jsonObject.remove(PackageKeys.SUBMITTER.getKey());
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			String submitterJsonString = user.generateJSONForApp();
			JSONObject submitterJson = new JSONObject(submitterJsonString);
			submitterJson.remove(PackageKeys.SHIBID.getKey());
			jsonObject.put(PackageKeys.SUBMITTER.getKey(), submitterJson);
		}
		return jsonObject;
	}

	public Package findByPackageId(String packageId) {
		return repo.findByPackageId(packageId);
	}
}
