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

	private static final MessageFormat fileUploadStartTiming = new MessageFormat("Timing|start|{0}|{1}|{2}|{3} files");
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private PackageRepository repo;
	private MongoTemplate mongoTemplate;
	private UniversalIdGenerator universalIdGenerator;
	private UserRepository userRepository;
	private JsonWriterSettingsConstructor jsonSettings;

	@Autowired
	public CustomPackageRepository(PackageRepository repo, MongoTemplate mongoTemplate,
			UniversalIdGenerator universalIdGenerator, UserRepository userRepo,
			JsonWriterSettingsConstructor jsonSettings) {
		this.repo = repo;
		this.mongoTemplate = mongoTemplate;
		this.universalIdGenerator = universalIdGenerator;
		this.userRepository = userRepo;
		this.jsonSettings = jsonSettings;
	}

	public String saveDynamicForm(JSONObject packageMetadata) throws JSONException {
		Date startTime = new Date();
		String packageId = universalIdGenerator.generateUniversalId();
		String submitterEmail = packageMetadata.getString(PackageKeys.SUBMITTER_EMAIL.getKey());
		JSONArray files = packageMetadata.getJSONArray(PackageKeys.FILES.getKey());

		log.info(fileUploadStartTiming.format(new Object[] { startTime, submitterEmail, packageId, files.length() }));

		for (int i = 0; i < files.length(); i++) {
			JSONObject file = files.getJSONObject(i);
			file.put(PackageKeys.ID.getKey(), universalIdGenerator.generateUniversalId());
		}

		User user = findUser(packageMetadata, submitterEmail);
		cleanUpObject(packageMetadata);

		DBRef userRef = new DBRef(USERS_COLLECTION, new ObjectId(user.getId()));
		String jsonString = packageMetadata.toString();

		Document document = Document.parse(jsonString);
		document.put(PackageKeys.SUBMITTER.getKey(), userRef);
		document.put(PackageKeys.CREATED_AT.getKey(), startTime);
		document.put(PackageKeys.ID.getKey(), packageId);
		document.put(PackageKeys.REGENERATE_ZIP.getKey(), false);

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

	private User findUser(JSONObject packageMetadata, String submitterEmail) throws JSONException {
		User user = userRepository.findByEmail(submitterEmail);
		if (user == null) {
			User newUser = new User();
			JSONObject submitter = packageMetadata.getJSONObject(PackageKeys.SUBMITTER.getKey());
			if (submitter.has(PackageKeys.DISPLAY_NAME.getKey())) {
				newUser.setDisplayName(submitter.getString(PackageKeys.DISPLAY_NAME.getKey()));
			}
			newUser.setEmail(submitter.getString(PackageKeys.EMAIL.getKey()));
			newUser.setFirstName(submitter.getString(PackageKeys.FIRST_NAME.getKey()));
			newUser.setLastName(submitter.getString(PackageKeys.LAST_NAME.getKey()));
			user = userRepository.save(newUser);
		}
		return user;
	}

	public Package save(Package packageInfo) {
		return repo.save(packageInfo);
	}

	public List<JSONObject> findAll(boolean needsRegnerateZipField) throws JSONException, JsonProcessingException {
		Query query = new Query();
		query = query.with(new Sort(Sort.Direction.DESC, PackageKeys.CREATED_AT.getKey()));

		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry());
		DocumentCodec codec = new DocumentCodec(codecRegistry, new BsonTypeClassMap());

		List<Document> results = mongoTemplate.find(query, Document.class, PACKAGES_COLLECTION);
		List<JSONObject> jsons = new ArrayList<>();
		for (Document document : results) {
			JsonWriterSettings settings = jsonSettings.getSettings();
			String json = document.toJson(settings, codec);
			JSONObject jsonObject = setUserInformation(json);
			jsonObject = cleanUpPackageObject(jsonObject, needsRegnerateZipField);
			jsons.add(jsonObject);
		}

		return jsons;
	}

	public String getJSONByPackageId(String packageId) throws JSONException, JsonProcessingException {

		BasicDBObject query = new BasicDBObject();
		query.put(PackageKeys.ID.getKey(), packageId);

		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry());
		DocumentCodec codec = new DocumentCodec(codecRegistry, new BsonTypeClassMap());
		MongoDatabase db = mongoTemplate.getDb();
		MongoCollection<Document> collection = db.getCollection(PACKAGES_COLLECTION);

		Document document = collection.find(query).first();
		JsonWriterSettings settings = jsonSettings.getSettings();
		String json = document.toJson(settings, codec);

		JSONObject jsonObject = setUserInformation(json);
		jsonObject = cleanUpPackageObject(jsonObject, false);

		return jsonObject.toString();
	}

	private JSONObject cleanUpPackageObject(JSONObject json, boolean needsRegenerateZipField) throws JSONException {
		if (!needsRegenerateZipField) {
			json.remove(PackageKeys.REGENERATE_ZIP.getKey());
		}
		return json;
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
			jsonObject.put(PackageKeys.SUBMITTER.getKey(), submitterJson);
		}
		return jsonObject;
	}

	public Package findByPackageId(String packageId) {
		return repo.findByPackageId(packageId);
	}
}
