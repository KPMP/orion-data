package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.codecs.DocumentCodec;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.UniversalIdGenerator;
import org.kpmp.users.User;
import org.kpmp.users.UserRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

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
	private CustomPackageRepository repo;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		repo = new CustomPackageRepository(packageRepository, mongoTemplate, universalIdGenerator, userRepo);
	}

	@After
	public void tearDown() throws Exception {
		repo = null;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSaveDynamicForm_happyPath() throws Exception {
		JSONObject packageMetadata = mock(JSONObject.class);
		when(packageMetadata.toString()).thenReturn("{}");
		when(universalIdGenerator.generateUniversalId()).thenReturn("123").thenReturn("456");
		when(packageMetadata.getString("submitterEmail")).thenReturn("emailAddress");
		User user = mock(User.class);
		when(user.getId()).thenReturn("5c2f9e01cb5e710049f33121");
		when(userRepo.findByEmail("emailAddress")).thenReturn(user);
		JSONArray files = mock(JSONArray.class);
		when(files.length()).thenReturn(1);
		JSONObject file = mock(JSONObject.class);
		when(files.getJSONObject(0)).thenReturn(file);
		when(packageMetadata.getJSONArray("files")).thenReturn(files);
		MongoCollection<Document> mongoCollection = mock(MongoCollection.class);
		when(mongoTemplate.getCollection("packages")).thenReturn(mongoCollection);

		String packageId = repo.saveDynamicForm(packageMetadata);

		ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
		verify(mongoCollection).insertOne(documentCaptor.capture());
		assertEquals("123", packageId);
		verify(file).put("_id", "456");
		verify(packageMetadata).remove("submitterEmail");
		verify(packageMetadata).remove("submitterFirstName");
		verify(packageMetadata).remove("submitterLastName");
		verify(packageMetadata).remove("submitter");
		Document actualDocument = documentCaptor.getValue();
		assertEquals("123", actualDocument.get("_id"));
		assertNotNull(actualDocument.get("createdAt"));
		assertEquals(false, actualDocument.get("regenerateZip"));
		DBRef submitter = (DBRef) actualDocument.get("submitter");
		assertEquals("users", submitter.getCollectionName());
		ObjectId objectId = (ObjectId) submitter.getId();
		assertEquals(new ObjectId("5c2f9e01cb5e710049f33121"), objectId);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSaveDynamicForm_whenNewUser() throws Exception {
		JSONObject packageMetadata = mock(JSONObject.class);
		JSONObject mockSubmitter = mock(JSONObject.class);
		when(packageMetadata.toString()).thenReturn("{}");
		when(universalIdGenerator.generateUniversalId()).thenReturn("123").thenReturn("456");
		when(packageMetadata.getString("submitterEmail")).thenReturn("emailAddress");
		when(packageMetadata.getJSONObject("submitter")).thenReturn(mockSubmitter);
		when(mockSubmitter.getString("displayName")).thenReturn("displayName");
		when(mockSubmitter.getString("email")).thenReturn("emailAddress2");
		when(mockSubmitter.getString("firstName")).thenReturn("firstName");
		when(mockSubmitter.getString("lastName")).thenReturn("lastName");
		when(mockSubmitter.has("displayName")).thenReturn(true);
		User user = mock(User.class);
		when(user.getId()).thenReturn("5c2f9e01cb5e710049f33121");
		when(userRepo.save(any(User.class))).thenReturn(user);
		when(userRepo.findByEmail("emailAddress")).thenReturn(null);
		JSONArray files = mock(JSONArray.class);
		when(files.length()).thenReturn(1);
		JSONObject file = mock(JSONObject.class);
		when(files.getJSONObject(0)).thenReturn(file);
		when(packageMetadata.getJSONArray("files")).thenReturn(files);
		MongoCollection<Document> mongoCollection = mock(MongoCollection.class);
		when(mongoTemplate.getCollection("packages")).thenReturn(mongoCollection);

		String packageId = repo.saveDynamicForm(packageMetadata);

		ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
		verify(mongoCollection).insertOne(documentCaptor.capture());
		assertEquals("123", packageId);
		verify(file).put("_id", "456");
		verify(packageMetadata).remove("submitterEmail");
		verify(packageMetadata).remove("submitterFirstName");
		verify(packageMetadata).remove("submitterLastName");
		verify(packageMetadata).remove("submitter");
		Document actualDocument = documentCaptor.getValue();
		assertEquals("123", actualDocument.get("_id"));
		assertNotNull(actualDocument.get("createdAt"));
		assertEquals(false, actualDocument.get("regenerateZip"));
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

	}

	@Test
	public void testFindAll() {
		List<Package> packages = Arrays.asList(mock(Package.class));
		when(packageRepository.findAll(new Sort(Sort.Direction.DESC, "createdAt"))).thenReturn(packages);

		List<Package> result = repo.findAll();

		assertEquals(packages, result);
		verify(packageRepository).findAll(new Sort(Sort.Direction.DESC, "createdAt"));
	}

	@Test
	public void testSave() {
		Package expectedPackage = mock(Package.class);
		Package packageInfo = expectedPackage;
		when(packageRepository.save(packageInfo)).thenReturn(expectedPackage);

		Package savedPackage = repo.save(packageInfo);

		verify(packageRepository).save(packageInfo);
		assertEquals(expectedPackage, savedPackage);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetJSONByPackageId() throws Exception {
		MongoDatabase db = mock(MongoDatabase.class);
		when(mongoTemplate.getDb()).thenReturn(db);
		MongoCollection<Document> mongoCollection = mock(MongoCollection.class);
		when(db.getCollection("packages")).thenReturn(mongoCollection);
		FindIterable<Document> result = mock(FindIterable.class);
		when(mongoCollection.find(any(BasicDBObject.class))).thenReturn(result);
		Document document = mock(Document.class);
		when(document.toJson(any(DocumentCodec.class))).thenReturn(
				"{ \"key\": \"value\", \"submitter\": { $id: { $oid: '123' }}, \"regenerateZip\": true, \"createdAt\": { $date: 123567 } }");
		when(result.first()).thenReturn(document);
		User user = mock(User.class);
		when(user.generateJSON()).thenReturn("{user: information, exists: here}");
		when(userRepo.findById("123")).thenReturn(Optional.of(user));

		String packageJson = repo.getJSONByPackageId("123");

		assertEquals(
				"{\"submitter\":{\"exists\":\"here\",\"user\":\"information\"},\"createdAt\":\"1970-01-01 00:02:03\",\"key\":\"value\"}",
				packageJson);
		ArgumentCaptor<BasicDBObject> queryCaptor = ArgumentCaptor.forClass(BasicDBObject.class);
		verify(mongoCollection).find(queryCaptor.capture());
		assertEquals("123", queryCaptor.getValue().get("_id"));
	}

}
