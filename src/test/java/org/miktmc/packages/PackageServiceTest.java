package org.miktmc.packages;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.miktmc.users.User;
import org.miktmc.logging.LoggingService;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class PackageServiceTest {

	@Mock
	private CustomPackageRepository packageRepository;
	@Mock
	private PackageFileHandler packageFileHandler;
	@Mock
	private FilePathHelper filePathHelper;
	private PackageService service;
	@Mock
	private LoggingService logger;
	@Mock
	private StateHandlerService stateHandlerService;
	private AutoCloseable mocks;

	@BeforeEach
	public void setUp() throws Exception {
		mocks = MockitoAnnotations.openMocks(this);
		service = new PackageService(packageFileHandler, filePathHelper, packageRepository, stateHandlerService, logger);
		ReflectionTestUtils.setField(service, "uploadSucceededState", "UPLOAD_SUCCEEDED");
		ReflectionTestUtils.setField(service, "packageTypeToExclude", "Electron Microscopy Imaging");
        ReflectionTestUtils.setField(service, "basePath", "/data/dataLake");
	}

	@AfterEach
	public void tearDown() throws Exception {
		mocks.close();
		service = null;
	}

	@Test
	public void testSendStateChangeEvent() throws Exception {
		service.sendStateChangeEvent("packageId1", "stateString", null, "codicil", "hostname");

		verify(stateHandlerService).sendStateChange("packageId1", "stateString", null, "codicil", "hostname");
	}

	@Test
	public void testFindAllPackages() throws JSONException, IOException {
		State newState = mock(State.class);
		HashMap<String, State> stateMap = new HashMap<String, State>();
		stateMap.put("packageId", newState);
		stateMap.put("anotherId", mock(State.class));
		when(stateHandlerService.getState()).thenReturn(stateMap);
		JSONObject uploadedPackage = mock(JSONObject.class);
		when(uploadedPackage.toString()).thenReturn("");
		when(uploadedPackage.getString("_id")).thenReturn("packageId");
		List<JSONObject> expectedResults = Arrays.asList(uploadedPackage);
		when(packageRepository.findAll()).thenReturn(expectedResults);

		List<PackageView> packages = service.findAllPackages();

		assertEquals(newState, packages.get(0).getState());
		verify(packageRepository).findAll();
		verify(stateHandlerService).getState();
	}

	@Test
	public void testfindMostPackages() throws JSONException, IOException {
		State newState = mock(State.class);
		HashMap<String, State> stateMap = new HashMap<String, State>();
		stateMap.put("packageId", newState);
		stateMap.put("anotherId", mock(State.class));
		when(stateHandlerService.getState()).thenReturn(stateMap);
		JSONObject excludedPackage = mock(JSONObject.class);
		when(excludedPackage.toString()).thenReturn("");
		when(excludedPackage.getString("_id")).thenReturn("packageId");
		when(excludedPackage.getString("packageType")).thenReturn("Electron Microscopy Imaging");
		JSONObject uploadedPackage = mock(JSONObject.class);
		when(uploadedPackage.toString()).thenReturn("");
		when(uploadedPackage.getString("_id")).thenReturn("packageId");
		when(uploadedPackage.getString("packageType")).thenReturn("Anything Else");
		when(packageRepository.findAll()).thenReturn(Arrays.asList(excludedPackage, uploadedPackage));

		List<PackageView> packages = service.findMostPackages();

		assertEquals(1, packages.size());
		assertEquals(newState, packages.get(0).getState());
		verify(packageRepository).findAll();
		verify(stateHandlerService).getState();
	}


	@Test
	public void testSavePackageInformation() throws Exception {
		JSONObject packageMetadata = mock(JSONObject.class);
		User user = mock(User.class);
		Package myPackage = new Package();
		myPackage.setPackageId("awesomeNewId");
		when(packageRepository.findByPackageId("awesomeNewId")).thenReturn(myPackage);
		String packageId = service.savePackageInformation(packageMetadata, user, "awesomeNewId");
		assertEquals("awesomeNewId", packageId);
		verify(packageRepository).saveDynamicForm(packageMetadata, user, "awesomeNewId");
	}

	@Test
	public void testFindPackage() throws Exception {
		Package expectedPackage = mock(Package.class);
		when(packageRepository.findByPackageId("packageId")).thenReturn(expectedPackage);

		Package actualPackage = service.findPackage("packageId");

		assertEquals(expectedPackage, actualPackage);
		verify(packageRepository).findByPackageId("packageId");
	}

	@Test
	public void testSaveFile_whenFilenameMetadataJson() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		boolean shouldAppend = false;

		service.saveFile(file, "packageId", "metadata.json", "study", shouldAppend);

		verify(packageFileHandler).saveMultipartFile(file, "packageId", "metadata_user.json", "study", shouldAppend);
	}

	@Test
	public void testSaveFile_whenInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		Package packageToUpdate = mock(Package.class);
		when(packageRepository.findByPackageId("packageId")).thenReturn(packageToUpdate);
		boolean shouldAppend = false;

		service.saveFile(file, "packageId", "filename", "study", shouldAppend);

		verify(packageFileHandler).saveMultipartFile(file, "packageId", "filename", "study", shouldAppend);
	}

	@SuppressWarnings({ "unchecked" })
	@Test
	public void testSaveFile_whenNotInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		Package packageToUpdate = mock(Package.class);
		when(packageRepository.findByPackageId("packageId")).thenReturn(packageToUpdate);
		ArrayList<Attachment> files = mock(ArrayList.class);
		when(packageToUpdate.getAttachments()).thenReturn(files);
		boolean shouldAppend = true;

		service.saveFile(file, "packageId", "filename", "study", shouldAppend);

		verify(packageFileHandler).saveMultipartFile(file, "packageId", "filename", "study", shouldAppend);
		verify(packageRepository, times(0)).findByPackageId("packageId");
	}

	@Test
	public void testSaveFile_whenException() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		boolean shouldAppend = true;
		Exception expectedException = new IOException();
		doThrow(expectedException).when(packageFileHandler).saveMultipartFile(file, "packageId", "filename", "study",
				shouldAppend);

		try {
			service.saveFile(file, "packageId", "filename", "study", shouldAppend);
			fail("Should have thrown exception");
		} catch (Exception actual) {
			assertEquals(expectedException, actual);
		}
	}

	// It is unfortunate, but this test works well locally, but does not work on
	// Travis. The log messages from inside the thread are not found when running on
	// Travis, but are found in my local environment. So, in order to make Travis
	// happy, I am commenting this out. This is a good test, and could be reused
	// when we move the zippingto a separate service perhaps.
//	@SuppressWarnings("rawtypes")
//	@Test
//	public void testCreateZipFile_logsUploadTimingCorrectly() throws Exception {
//		Package packageInfo = mock(Package.class);
//		User user = mock(User.class);
//		when(user.getEmail()).thenReturn("emailAddress");
//		when(packageInfo.getSubmitter()).thenReturn(user);
//		Attachment attachment1 = new Attachment();
//		attachment1.setSize(55555555);
//		Attachment attachment2 = new Attachment();
//		attachment2.setSize(6666);
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.SECOND, -10);
//		when(packageInfo.getCreatedAt()).thenReturn(new Date(calendar.getTimeInMillis()));
//		when(packageInfo.getAttachments()).thenReturn(Arrays.asList(attachment1, attachment2));
//		when(packageRepository.findByPackageId("123")).thenReturn(packageInfo);
//
//		service.createZipFile("123", "userId");
//
//		ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
//		ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(String.class);
//		ArgumentCaptor<String> packageIdCaptor = ArgumentCaptor.forClass(String.class);
//		ArgumentCaptor<String> uriCaptor = ArgumentCaptor.forClass(String.class);
//		ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
//		verify(logger, times(3)).logInfoMessage(classCaptor.capture(), userIdCaptor.capture(),
//				packageIdCaptor.capture(), uriCaptor.capture(), messageCaptor.capture());
//		assertEquals(PackageService.class, classCaptor.getAllValues().get(0));
//		assertEquals("userId", userIdCaptor.getAllValues().get(0));
//		assertEquals("123", packageIdCaptor.getAllValues().get(0));
//		assertEquals("PackageService.createZipFile", uriCaptor.getAllValues().get(0));
//		String timingMessage = messageCaptor.getAllValues().get(0);
//		assertEquals(true, timingMessage.startsWith("Timing|end|"));
//		assertEquals(true, timingMessage.contains("|emailAddress|"));
//		assertEquals(true, timingMessage.contains("|123|"));
//		assertEquals(true, timingMessage.contains("|2 files|"));
//		String displaySize = FileUtils.byteCountToDisplaySize(55555555 + 6666);
//		assertEquals(true, timingMessage.contains("|" + displaySize + "|"));
//		assertEquals(true, timingMessage.contains("|10 seconds|"));
//		assertEquals(true, timingMessage.contains("|5.299 MB/sec"));
//		assertEquals(PackageService.class, classCaptor.getAllValues().get(1));
//		assertEquals("userId", userIdCaptor.getAllValues().get(1));
//		assertEquals("123", packageIdCaptor.getAllValues().get(1));
//		assertEquals("PackageService.createZipFile", uriCaptor.getAllValues().get(1));
//		assertEquals("Zip file created for package:  123", messageCaptor.getAllValues().get(1));
//		assertEquals(PackageService.class, classCaptor.getAllValues().get(2));
//		assertEquals("userId", userIdCaptor.getAllValues().get(2));
//		assertEquals("123", packageIdCaptor.getAllValues().get(2));
//		assertEquals("PackageService.createZipFile", uriCaptor.getAllValues().get(2));
//		timingMessage = messageCaptor.getAllValues().get(2);
//		System.err.println(timingMessage);
//		assertEquals(true, timingMessage.startsWith("Timing|zip|"));
//		assertEquals(true, timingMessage.contains("|emailAddress|"));
//		assertEquals(true, timingMessage.contains("|123|"));
//		assertEquals(true, timingMessage.contains("|2 files|"));
//		displaySize = FileUtils.byteCountToDisplaySize(55555555 + 6666);
//		assertEquals(true, timingMessage.contains("|" + displaySize + "|"));
//		assertEquals(true, timingMessage.contains("|0 seconds"));
//	}

	@Test
	public void testValidateFileLengthsMatch_whenMatch() throws Exception {
		Logger testLogger = (Logger) LoggerFactory.getLogger(PackageService.class);
		ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
		listAppender.start();
		testLogger.addAppender(listAppender);
		Path packagePath = Files.createTempDirectory("data");
		packagePath.toFile().deleteOnExit();
		String file1Path = Paths.get(packagePath.toString(), "file1").toString();
		String file2Path = Paths.get(packagePath.toString(), "file2").toString();
		File file1 = new File(file1Path);
		File file2 = new File(file2Path);
		file1.createNewFile();
		file1.deleteOnExit();
		file2.createNewFile();
		file2.deleteOnExit();
		Attachment attachment1 = new Attachment();
		attachment1.setFileName("file1");
		attachment1.setSize(file1.length());
		Attachment attachment2 = new Attachment();
		attachment2.setFileName("file2");
		attachment2.setSize(file2.length());
		List<Attachment> attachments = Arrays.asList(attachment1, attachment2);
		User user = mock(User.class);

		assertEquals(true, service.validateFileLengthsMatch(attachments, packagePath.toString(), "packageId", user));
		List<ILoggingEvent> logsList = listAppender.list;
		assertEquals(0, logsList.size());
		verify(logger, times(0)).logErrorMessage(PackageService.class, user, "packageId",
				"PackageService.validateFileLengthsMatch",
				"ERROR|zip|File size in metadata does not match file size on disk for file: file2");
	}

	@Test
	public void testValidateFileLengthsMatch_whenNoMatch() throws Exception {
		Path packagePath = Files.createTempDirectory("data");
		packagePath.toFile().deleteOnExit();
		String file1Path = Paths.get(packagePath.toString(), "file1").toString();
		String file2Path = Paths.get(packagePath.toString(), "file2").toString();
		File file1 = new File(file1Path);
		File file2 = new File(file2Path);
		file1.createNewFile();
		file1.deleteOnExit();
		file2.createNewFile();
		file2.deleteOnExit();
		Attachment attachment1 = new Attachment();
		attachment1.setFileName("file1");
		attachment1.setSize(file1.length());
		Attachment attachment2 = new Attachment();
		attachment2.setFileName("file2");
		attachment2.setSize(1234l);
		List<Attachment> attachments = Arrays.asList(attachment1, attachment2);
		User user = mock(User.class);

		assertEquals(false, service.validateFileLengthsMatch(attachments, packagePath.toString(), "packageId", user));
		verify(logger).logErrorMessage(PackageService.class, user, "packageId",
				"PackageService.validateFileLengthsMatch",
				"ERROR|zip|File size in metadata does not match file size on disk for file: file2");

	}

	@Test
	public void testCheckFilesExistTrue() throws Exception {
		User user = mock(User.class);

		assertEquals(true, service.checkFilesExist(Arrays.asList("file1", "file2"), Arrays.asList("file1", "file2"),
				"packageId", user));
		verify(logger, times(0)).logErrorMessage(PackageService.class, user, "packageId",
				"PackageService.checkFilesExist", "ERROR|zip|File list in metadata does not match file list on disk");
	}

	@Test
	public void testDelete() throws Exception {
		Path packagePath = Files.createTempDirectory("data");
		packagePath.toFile().deleteOnExit();
		String file1Path = Paths.get(packagePath.toString(), "file1").toString();
		File tempFile1 = new File(file1Path);
		tempFile1.createNewFile();
		tempFile1.deleteOnExit();
		Package testPackage = new Package();
		testPackage.setPackageId("testid");
		testPackage.setStudy("Neptune");
		ArrayList<Attachment> files = new ArrayList<>();
		Attachment file1 = new Attachment();
		file1.setFileName("file1");
		file1.setId("id1");
		Attachment file2 = new Attachment();
		file2.setFileName("filename2.txt");
		file2.setId("id2");
		files.add(file1);
		files.add(file2);
		testPackage.setAttachments(files);
		when(filePathHelper.getFilePath("testid", "Neptune", "file1")).thenReturn("/data/file1");
		when(packageRepository.findByPackageId("testid")).thenReturn(testPackage);
		Boolean result = service.deleteFile("testid", "id1", "shibid");
		assertEquals(1, testPackage.getAttachments().size());
	}

	@Test
	public void testCheckFilesExistFalse() throws Exception {
		User user = mock(User.class);

		assertEquals(false, service.checkFilesExist(Arrays.asList("file1", "file2"),
				Arrays.asList("file1", "file2, file3"), "packageId", user));
		verify(logger).logErrorMessage(PackageService.class, user, "packageId", "PackageService.checkFilesExist",
				"ERROR|zip|File list in metadata does not match file list on disk: file1,file2, file3 vs file1,file2");
	}

    @Test 
    public void testStripMetadata() throws Exception {
        Path rootPath = Files.createTempDirectory("dataLake");
        Path packagePath = Files.createTempDirectory(rootPath, "study");
        String studyDir = packagePath.getFileName().toString();
        packagePath.toFile().deleteOnExit();
        String file1Path = Paths.get(packagePath.toString(), "file1.jpg").toString();
        String file2Path = Paths.get(packagePath.toString(), "file2.png").toString();
        File file1 = new File(file1Path);
        File file2 = new File(file2Path);
        file1.createNewFile();
        file1.deleteOnExit();
        file2.createNewFile();
        file2.deleteOnExit();
        Attachment attachment1 = new Attachment();
        attachment1.setFileName("file1.jpg");
        attachment1.setSize(file1.length());
        Attachment attachment2 = new Attachment();
        attachment2.setFileName("file2.png");
        attachment2.setSize(file2.length());
        List<Attachment> attachments = Arrays.asList(attachment1, attachment2);
        Package newPackage = new Package();
        newPackage.setPackageId("1234");
        newPackage.setAttachments(attachments);
        when(filePathHelper.getFilePath("1234", studyDir, "file1")).thenReturn(file1Path);
        when(filePathHelper.getFilePath("1234", studyDir, "file2")).thenReturn(file2Path);
        when(service.findPackage(newPackage.getPackageId())).thenReturn(newPackage);
        
        int testSuccessCode = service.stripMetadata(newPackage);

        assertEquals(1, testSuccessCode);
    }

	@Test
	public void testAddFiles() throws Exception {
		JSONArray fileArray = new JSONArray();
		JSONObject jFile1 = new JSONObject();
		jFile1.put(PackageKeys.ID.getKey(), "awesomeNewId");
		jFile1.put(PackageKeys.SIZE.getKey(), 123);
		jFile1.put(PackageKeys.FILE_NAME.getKey(), "old_name_1");
		jFile1.put(PackageKeys.ORIGINAL_FILE_NAME.getKey(), "old_name_1");
		JSONObject jFile2 = new JSONObject();
		jFile2.put(PackageKeys.ID.getKey(), "awesomeNewId");
		jFile2.put(PackageKeys.SIZE.getKey(), 123);
		jFile2.put(PackageKeys.FILE_NAME.getKey(), "old_name_2");
		jFile2.put(PackageKeys.ORIGINAL_FILE_NAME.getKey(), "old_name_2");
		fileArray.put(jFile1);
		fileArray.put(jFile2);
		Package myPackage = new Package();
		myPackage.setPackageId("awesomeNewId");
		List<Attachment> files = new ArrayList<>();
		Attachment file1 = new Attachment();
		file1.setOriginalFileName("old_name_1");
		Attachment file2 = new Attachment();
		file2.setOriginalFileName("old_name_3");
		files.add(file1);
		files.add(file2);
		myPackage.setAttachments(files);
		when(service.findPackage("awesomeNewId")).thenReturn(myPackage);
		assertEquals(fileArray.length(), 2);
		List<Attachment> resultFiles = service.addFiles("awesomeNewId", fileArray, "shibid", false);
		assertEquals(fileArray.length(), 1);
		verify(packageRepository).setRenamedFiles(fileArray, null, null);
		assertEquals(3, resultFiles.size());
		verify(packageRepository).updateField("awesomeNewId", "files", myPackage.getAttachments());
		verify(packageRepository, times(1)).addModification("awesomeNewId", "shibid", "ADD");

	}

	@Test
	public void testCanReplace() throws Exception {
		Package myPackage = new Package();
		myPackage.setPackageId("awesomePackageId");
		List<Attachment> files = new ArrayList<>();
		Attachment file1 = new Attachment();
		file1.setOriginalFileName("old_name_1");
		file1.setId("awesomeNewId1");
		Attachment file2 = new Attachment();
		file2.setOriginalFileName("old_name_2");
		file2.setId("awesomeNewId2");
		files.add(file1);
		files.add(file2);
		myPackage.setAttachments(files);
		when(service.findPackage("awesomePackageId")).thenReturn(myPackage);
		assertTrue(service.canReplaceFile("awesomePackageId","awesomeNewId1", "old_name_1"));
		assertFalse(service.canReplaceFile("awesomePackageId","awesomeNewId2", "old_name_1"));
		assertTrue(service.canReplaceFile("awesomePackageId","awesomeNewId2", "old_name_5"));
	}

	@Test
	public void testSetPackageValidated() throws Exception {
		Package myPackage = new Package();
		myPackage.setPackageId("awesomePackageId");
		List<Attachment> files = new ArrayList<>();
		Attachment file1 = new Attachment();
		file1.setOriginalFileName("old_name_1");
		file1.setValidated(false);
		file1.setId("awesomeNewId1");
		Attachment file2 = new Attachment();
		file2.setOriginalFileName("old_name_2");
		file2.setId("awesomeNewId2");
		file2.setValidated(false);
		files.add(file1);
		files.add(file2);
		myPackage.setAttachments(files);
		when(service.findPackage("awesomePackageId")).thenReturn(myPackage);
		assertFalse(file1.getValidated());
		assertFalse(file1.getValidated());
		service.setPackageValidated("awesomePackageId");
		assertTrue(file1.getValidated());
		assertTrue(file2.getValidated());
	}

}
