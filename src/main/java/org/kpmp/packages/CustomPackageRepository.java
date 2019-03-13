package org.kpmp.packages;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.bson.Document;
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
import org.springframework.stereotype.Component;

import com.mongodb.DBRef;
import com.mongodb.client.MongoCollection;

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

	private static final String CREATED_AT_FIELD = "createdAt";
	private static final String MONGO_ID_FIELD = "_id";

	private static final MessageFormat fileUploadStartTiming = new MessageFormat("Timing|start|{0}|{1}|{2}|{3} files");
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private PackageRepository repo;
	private MongoTemplate mongoTemplate;
	private UniversalIdGenerator universalIdGenerator;
	private UserRepository userRepository;

	@Autowired
	public CustomPackageRepository(PackageRepository repo, MongoTemplate mongoTemplate,
			UniversalIdGenerator universalIdGenerator, UserRepository userRepo) {
		this.repo = repo;
		this.mongoTemplate = mongoTemplate;
		this.universalIdGenerator = universalIdGenerator;
		this.userRepository = userRepo;
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
		packageMetadata.put(MONGO_ID_FIELD, packageId);
		packageMetadata.put(REGENERATE_ZIP_KEY, false);
		User user = userRepository.findByEmail(submitterEmail);
		if (user == null) {
			User newUser = new User();
			JSONObject submitter = packageMetadata.getJSONObject(SUBMITTER_OBJECT_KEY);
			newUser.setDisplayName(submitter.getString(DISPLAY_NAME_KEY));
			newUser.setEmail(submitter.getString(EMAIL_KEY));
			newUser.setFirstName(submitter.getString(FIRST_NAME_KEY));
			newUser.setLastName(submitter.getString(LAST_NAME_KEY));
			user = userRepository.save(newUser);
		}
		packageMetadata.remove(SUBMITTER_OBJECT_KEY);
		packageMetadata.remove(SUBMITTER_FIRST_NAME_KEY);
		packageMetadata.remove(SUBMITTER_LAST_NAME_KEY);
		packageMetadata.remove(SUBMITTER_EMAIL_KEY);

		DBRef userRef = new DBRef(USERS_COLLECTION, new ObjectId(user.getId()));
		String jsonString = packageMetadata.toString();

		Document document = Document.parse(jsonString);
		document.put(SUBMITTER_OBJECT_KEY, userRef);
		document.put(CREATED_AT_FIELD, startTime);

		MongoCollection<Document> collection = mongoTemplate.getCollection(PACKAGES_COLLECTION);
		collection.insertOne(document);

		return packageId;
	}

	public List<Package> findAll() {
		return repo.findAll(new Sort(Sort.Direction.DESC, CREATED_AT_FIELD));
	}

	public Package save(Package packageInfo) {
		return repo.save(packageInfo);
	}

	public Package findByPackageId(String packageId) {
		return repo.findByPackageId(packageId);
	}

}
