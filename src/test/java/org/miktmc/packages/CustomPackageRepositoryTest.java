package org.miktmc.packages;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.logging.LoggingService;
import org.miktmc.users.User;
import org.miktmc.users.UserRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;
import com.mongodb.DBRef;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class CustomPackageRepositoryTest {

	@Mock
	private PackageRepository packageRepository;
	@Mock
	private MongoTemplate mongoTemplate;
	@Mock
	private UniversalIdGenerator universalIdGenerator;
	@Mock
	private UserRepository userRepo;
	@Mock
	private JsonWriterSettingsConstructor jsonWriterSettings;
	@Mock
	private LoggingService logger;
	@Mock
	private StudyFileInfoRepository studyFileInfoRepository;
	private CustomPackageRepository repo;
	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		repo = new CustomPackageRepository(packageRepository, mongoTemplate, universalIdGenerator, userRepo,
				jsonWriterSettings, studyFileInfoRepository, logger);
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		repo = null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSaveDynamicForm_happyPath() throws Exception {
		JSONObject packageMetadata = mock(JSONObject.class);
		when(packageMetadata.toString()).thenReturn("{\"site\":\"CureGN Site\"}");
		when(packageMetadata.getString("study")).thenReturn("curegn");
		when(packageMetadata.getString("biopsyId")).thenReturn("2344");
		when(universalIdGenerator.generateUniversalId()).thenReturn("456");
		when(packageMetadata.getString("submitterEmail")).thenReturn("emailAddress");
		User user = mock(User.class);
		when(user.getEmail()).thenReturn("emailAddress");
		when(user.getId()).thenReturn("5c2f9e01cb5e710049f33121");
		when(userRepo.findByEmail("emailAddress")).thenReturn(user);
		JSONArray files = mock(JSONArray.class);
		when(files.length()).thenReturn(1);
		JSONObject file = mock(JSONObject.class);
		when(file.getString("fileName")).thenReturn("orignalFileName.txt");
		when(files.getJSONObject(0)).thenReturn(file);
		when(packageMetadata.getJSONArray("files")).thenReturn(files);
		MongoCollection<Document> mongoCollection = mock(MongoCollection.class);
		when(mongoTemplate.getCollection("packages")).thenReturn(mongoCollection);
		StudyFileInfo studyFileInfo = mock (StudyFileInfo.class);
		when(studyFileInfo.getShouldRename()).thenReturn(true);
		when(studyFileInfo.getFileCounter()).thenReturn(54);
		when(studyFileInfo.getUploadSourceLetter()).thenReturn("D")
;		when(studyFileInfoRepository.findByStudy("curegn")).thenReturn(studyFileInfo);

		String packageId = repo.saveDynamicForm(packageMetadata, user, "123");

		ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
		verify(mongoCollection).insertOne(documentCaptor.capture());
		assertEquals("123", packageId);
		verify(file).put("_id", "456");
		verify(file).put("originalFileName", "orignalFileName.txt");
		verify(file).put("fileName", "2344_D_54.txt");
		verify(studyFileInfo).setFileCounter(55);
		verify(studyFileInfoRepository).save(studyFileInfo);
		verify(packageMetadata).remove("submitterEmail");
		verify(packageMetadata).remove("submitterFirstName");
		verify(packageMetadata).remove("submitterLastName");
		verify(packageMetadata).remove("submitter");
		Document actualDocument = documentCaptor.getValue();
		assertEquals("123", actualDocument.get("_id"));
		assertEquals("CureGN Site", actualDocument.get("site"));
		assertNotNull(actualDocument.get("createdAt"));
		DBRef submitter = (DBRef) actualDocument.get("submitter");
		assertEquals("users", submitter.getCollectionName());
		ObjectId objectId = (ObjectId) submitter.getId();
		assertEquals(new ObjectId("5c2f9e01cb5e710049f33121"), objectId);
		ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<String> packageIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
		verify(logger).logInfoMessage(classCaptor.capture(), userCaptor.capture(), packageIdCaptor.capture(),
				uriCaptor.capture(), messageCaptor.capture());
		assertEquals(CustomPackageRepository.class, classCaptor.getValue());
		assertEquals(user, userCaptor.getValue());
		assertEquals(packageId, packageIdCaptor.getValue());
		assertEquals("CustomPackageRepository.saveDynamicForm", uriCaptor.getValue());
		assertEquals(true, messageCaptor.getValue().startsWith("Timing|start|"));
		assertEquals(true, messageCaptor.getValue().endsWith("|emailAddress|123|1 files"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSaveDynamicForm_whenNewUser() throws Exception {
		JSONObject packageMetadata = mock(JSONObject.class);
		when(packageMetadata.getString("study")).thenReturn("neptune");
		when(packageMetadata.toString()).thenReturn("{}");
		when(universalIdGenerator.generateUniversalId()).thenReturn("456");
		User user = mock(User.class);
		when(user.getDisplayName()).thenReturn("displayName");
		when(user.getEmail()).thenReturn("emailAddress2");
		when(user.getFirstName()).thenReturn("firstName");
		when(user.getLastName()).thenReturn("lastName");
		when(user.getId()).thenReturn("5c2f9e01cb5e710049f33121");
		when(userRepo.save(any(User.class))).thenReturn(user);
		when(userRepo.findByEmail("emailAddress2")).thenReturn(null);
		JSONArray files = mock(JSONArray.class);
		when(files.length()).thenReturn(1);
		JSONObject file = mock(JSONObject.class);
		when(files.getJSONObject(0)).thenReturn(file);
		when(packageMetadata.getJSONArray("files")).thenReturn(files);
		MongoCollection<Document> mongoCollection = mock(MongoCollection.class);
		when(mongoTemplate.getCollection("packages")).thenReturn(mongoCollection);
		StudyFileInfo studyFileInfo = mock (StudyFileInfo.class);
		when(studyFileInfo.getShouldRename()).thenReturn(false);
		when(studyFileInfoRepository.findByStudy("neptune")).thenReturn(studyFileInfo);

		String packageId = repo.saveDynamicForm(packageMetadata, user, "123");

		ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
		verify(mongoCollection).insertOne(documentCaptor.capture());
		assertEquals("123", packageId);
		verify(file).put("_id", "456");
		verify(packageMetadata).remove("submitterEmail");
		verify(packageMetadata).remove("submitterFirstName");
		verify(packageMetadata).remove("submitterLastName");
		verify(packageMetadata).remove("submitter");
		verify(studyFileInfo, times(0)).setFileCounter(anyInt());
		verify(studyFileInfoRepository).save(studyFileInfo);
		Document actualDocument = documentCaptor.getValue();
		assertEquals("123", actualDocument.get("_id"));
		assertNotNull(actualDocument.get("createdAt"));
		DBRef submitter = (DBRef) actualDocument.get("submitter");
		assertEquals("users", submitter.getCollectionName());
		ObjectId objectId = (ObjectId) submitter.getId();
		assertEquals(new ObjectId("5c2f9e01cb5e710049f33121"), objectId);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepo).save(userCaptor.capture());
		User actualUser = userCaptor.getValue();
		assertEquals("displayName", actualUser.getDisplayName());
		assertEquals("emailAddress2", actualUser.getEmail());
		assertEquals("firstName", actualUser.getFirstName());
		assertEquals("lastName", actualUser.getLastName());
		ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
		ArgumentCaptor<String> packageIdCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
		verify(logger).logInfoMessage(classCaptor.capture(), userCaptor.capture(), packageIdCaptor.capture(),
				uriCaptor.capture(), messageCaptor.capture());
		assertEquals(CustomPackageRepository.class, classCaptor.getValue());
		assertEquals(user, userCaptor.getValue());
		assertEquals(packageId, packageIdCaptor.getValue());
		assertEquals("CustomPackageRepository.saveDynamicForm", uriCaptor.getValue());
		assertEquals(true, messageCaptor.getValue().startsWith("Timing|start|"));
		assertEquals(true, messageCaptor.getValue().endsWith("|emailAddress2|123|1 files"));
	}

	@SuppressWarnings({ "unchecked" })
	@Test
	public void testGetJSONByPackageId() throws Exception {
		MongoDatabase db = mock(MongoDatabase.class);
		when(mongoTemplate.getDb()).thenReturn(db);
		MongoCollection<Document> mongoCollection = mock(MongoCollection.class);
		when(db.getCollection("packages")).thenReturn(mongoCollection);
		FindIterable<Document> result = mock(FindIterable.class);
		when(mongoCollection.find(any(BasicDBObject.class))).thenReturn(result);
		Document document = mock(Document.class);

		when(jsonWriterSettings.getSettings()).thenReturn(JsonWriterSettings.builder().build());
		when(document.toJson(any(JsonWriterSettings.class), any(DocumentCodec.class))).thenReturn(
				"{ \"_id\": \"123\", \"key\": \"value with /\", \"submitter\": { $id: { $oid: '123' }, \"shibId\": \"555\"}, \"createdAt\": { $date: 123567 } }");
		when(result.first()).thenReturn(document);
		User user = mock(User.class);
		when(user.generateJSONForApp()).thenReturn("{ $id: { $oid: '123' }, 'shibId': '555', 'firstName': 'John'}");
		when(userRepo.findById("123")).thenReturn(Optional.of(user));

		String packageJson = repo.getJSONByPackageId("123");

		assertEquals(
				"{\"createdAt\":{\"$date\":123567},\"submitter\":{\"firstName\":\"John\",\"$id\":{\"$oid\":\"123\"}},\"_id\":\"123\",\"key\":\"value with /\"}",
				packageJson);
		ArgumentCaptor<BasicDBObject> queryCaptor = ArgumentCaptor.forClass(BasicDBObject.class);
		verify(mongoCollection).find(queryCaptor.capture());
		assertEquals("123", queryCaptor.getValue().get("_id"));

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testFindAll() throws Exception {
		Document firstResult = mock(Document.class);
		List<Document> results = Arrays.asList(firstResult);
		when(mongoTemplate.find(any(Query.class), any(Class.class), any(String.class))).thenReturn(results);
		when(jsonWriterSettings.getSettings()).thenReturn(JsonWriterSettings.builder().build());
		when(firstResult.toJson(any(JsonWriterSettings.class), any(DocumentCodec.class))).thenReturn(
				"{ \"_id\": \"123\", \"key\": \"value\", \"submitter\": { $id: { $oid: '123' } }, \"createdAt\": { $date: 123567 } }");

		List<JSONObject> allJsons = repo.findAll();

		assertEquals(1, allJsons.size());

		ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
		ArgumentCaptor<Class> entityCaptor = ArgumentCaptor.forClass(Class.class);
		ArgumentCaptor<String> collectionCaptor = ArgumentCaptor.forClass(String.class);
		verify(mongoTemplate).find(queryCaptor.capture(), entityCaptor.capture(), collectionCaptor.capture());
		assertEquals("packages", collectionCaptor.getValue());
		assertEquals(Document.class, entityCaptor.getValue());
	}

	@Test
	public void testUpdateField() throws Exception {
		repo.updateField("id", "thisField", "a value");

		ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
		ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
		ArgumentCaptor<String> collectionNameCaptor = ArgumentCaptor.forClass(String.class);
		verify(mongoTemplate).updateFirst(queryCaptor.capture(), updateCaptor.capture(),
				collectionNameCaptor.capture());
		assertEquals("packages", collectionNameCaptor.getValue());
		Query actualQuery = queryCaptor.getValue();
		Document queryObject = actualQuery.getQueryObject();
		assertEquals("id", queryObject.get(PackageKeys.ID.getKey()));
		Update updater = updateCaptor.getValue();
		Document updateObject = updater.getUpdateObject();
		Object actualDocumnet = updateObject.get("$set");
		Document expectedDocument = new Document();
		expectedDocument.append("thisField", "a value");
		assertEquals(expectedDocument, actualDocumnet);
	}

}
