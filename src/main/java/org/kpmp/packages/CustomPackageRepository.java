package org.kpmp.packages;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
import org.kpmp.UniversalIdGenerator;
import org.kpmp.users.User;
import org.kpmp.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@Component
public class CustomPackageRepository {

	private static final String PACKAGES_COLLECTION = "packages";
	private static final String USERS_COLLECTION = "users";

	private static final String DISPLAY_NAME_KEY = "displayName";
	private static final String EMAIL_KEY = "email";
	private static final String FILES_KEY = "files";
	private static final String FIRST_NAME_KEY = "firstName";
	private static final String LAST_NAME_KEY = "lastName";
	private static final String REGENERATE_ZIP_KEY = "regenerateZip";
	private static final String SUBMITTER_EMAIL_KEY = "submitterEmail";
	private static final String SUBMITTER_FIRST_NAME_KEY = "submitterFirstName";
	private static final String SUBMITTER_LAST_NAME_KEY = "submitterLastName";
	private static final String SUBMITTER_OBJECT_KEY = "submitter";
	private static final String SUBMITTER_ID_KEY = "$oid";
	private static final String SUBMITTER_ID_OBJECT_KEY = "$id";
	private static final String CREATED_AT_DATE_KEY = "$date";

	private static final String CREATED_AT_FIELD = "createdAt";
	private static final String MONGO_ID_FIELD = "_id";

	private static final MessageFormat fileUploadStartTiming = new MessageFormat("Timing|start|{0}|{1}|{2}|{3} files");
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private PackageRepository repo;
	private MongoTemplate mongoTemplate;
	private UniversalIdGenerator universalIdGenerator;
	private UserRepository userRepository;
	private JsonWriterSettingsClass jsonSettings;

	@Autowired
	public CustomPackageRepository(PackageRepository repo, MongoTemplate mongoTemplate,
			UniversalIdGenerator universalIdGenerator, UserRepository userRepo, JsonWriterSettingsClass jsonSettings) {
		this.repo = repo;
		this.mongoTemplate = mongoTemplate;
		this.universalIdGenerator = universalIdGenerator;
		this.userRepository = userRepo;
		this.jsonSettings = jsonSettings;
	}

	public String saveDynamicForm(JSONObject packageMetadata) throws JSONException {
		Date startTime = new Date();
		String packageId = universalIdGenerator.generateUniversalId();
		String submitterEmail = packageMetadata.getString(SUBMITTER_EMAIL_KEY);
		JSONArray files = packageMetadata.getJSONArray(FILES_KEY);

		log.info(fileUploadStartTiming.format(new Object[] { startTime, submitterEmail, packageId, files.length() }));

		for (int i = 0; i < files.length(); i++) {
			JSONObject file = files.getJSONObject(i);
			file.put(MONGO_ID_FIELD, universalIdGenerator.generateUniversalId());
		}

		User user = findUser(packageMetadata, submitterEmail);
		cleanUpObject(packageMetadata);

		DBRef userRef = new DBRef(USERS_COLLECTION, new ObjectId(user.getId()));
		String jsonString = packageMetadata.toString();

		Document document = Document.parse(jsonString);
		document.put(SUBMITTER_OBJECT_KEY, userRef);
		document.put(CREATED_AT_FIELD, startTime);
		document.put(MONGO_ID_FIELD, packageId);
		document.put(REGENERATE_ZIP_KEY, false);

		MongoCollection<Document> collection = mongoTemplate.getCollection(PACKAGES_COLLECTION);
		collection.insertOne(document);

		return packageId;
	}

	private void cleanUpObject(JSONObject packageMetadata) {
		packageMetadata.remove(SUBMITTER_OBJECT_KEY);
		packageMetadata.remove(SUBMITTER_FIRST_NAME_KEY);
		packageMetadata.remove(SUBMITTER_LAST_NAME_KEY);
		packageMetadata.remove(SUBMITTER_EMAIL_KEY);
	}

	private User findUser(JSONObject packageMetadata, String submitterEmail) throws JSONException {
		User user = userRepository.findByEmail(submitterEmail);
		if (user == null) {
			User newUser = new User();
			JSONObject submitter = packageMetadata.getJSONObject(SUBMITTER_OBJECT_KEY);
			if (submitter.has(DISPLAY_NAME_KEY)) {
				newUser.setDisplayName(submitter.getString(DISPLAY_NAME_KEY));
			}
			newUser.setEmail(submitter.getString(EMAIL_KEY));
			newUser.setFirstName(submitter.getString(FIRST_NAME_KEY));
			newUser.setLastName(submitter.getString(LAST_NAME_KEY));
			user = userRepository.save(newUser);
		}
		return user;
	}

	public List<Package> findAll() {
		return repo.findAll(new Sort(Sort.Direction.DESC, CREATED_AT_FIELD));
	}

	public Package save(Package packageInfo) {
		return repo.save(packageInfo);
	}

	public List<JSONObject> findAllJson() throws JSONException, JsonProcessingException {
		Query query = new Query();
		query = query.with(new Sort(Sort.Direction.DESC, CREATED_AT_FIELD));

		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry());
		DocumentCodec codec = new DocumentCodec(codecRegistry, new BsonTypeClassMap());

		List<Document> results = mongoTemplate.find(query, Document.class, PACKAGES_COLLECTION);
		List<JSONObject> jsons = new ArrayList<>();
		for (Document document : results) {
			JsonWriterSettings settings = jsonSettings.getSettings();
			String json = document.toJson(settings, codec);
			JSONObject jsonObject = setUserInformation(json);
			jsonObject = cleanUpPackageObject(jsonObject);
			jsons.add(jsonObject);
		}

		return jsons;
	}

	public String getJSONByPackageId(String packageId) throws JSONException, JsonProcessingException {

		BasicDBObject query = new BasicDBObject();
		query.put(MONGO_ID_FIELD, packageId);

		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry());
		DocumentCodec codec = new DocumentCodec(codecRegistry, new BsonTypeClassMap());
		MongoDatabase db = mongoTemplate.getDb();
		MongoCollection<Document> collection = db.getCollection(PACKAGES_COLLECTION);

		Document document = collection.find(query).first();
		JsonWriterSettings settings = jsonSettings.getSettings();
		String json = document.toJson(settings, codec);

		JSONObject jsonObject = setUserInformation(json);
		jsonObject = cleanUpPackageObject(jsonObject);

		return jsonObject.toString();
	}

	private JSONObject cleanUpPackageObject(JSONObject json) throws JSONException {
		json.remove(REGENERATE_ZIP_KEY);
		return json;
	}

	private JSONObject setUserInformation(String json) throws JSONException, JsonProcessingException {
		JSONObject jsonObject = new JSONObject(json);
		JSONObject submitter = (JSONObject) jsonObject.get(SUBMITTER_OBJECT_KEY);
		JSONObject submitterIdObject = (JSONObject) submitter.get(SUBMITTER_ID_OBJECT_KEY);
		String submitterId = submitterIdObject.getString(SUBMITTER_ID_KEY);
		Optional<User> userOptional = userRepository.findById(submitterId);
		jsonObject.remove(SUBMITTER_OBJECT_KEY);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			String submitterJsonString = user.generateJSON();
			JSONObject submitterJson = new JSONObject(submitterJsonString);
			jsonObject.put(SUBMITTER_OBJECT_KEY, submitterJson);
		}
		return jsonObject;
	}

	public Package findByPackageId(String packageId) {
		return repo.findByPackageId(packageId);
	}
}
