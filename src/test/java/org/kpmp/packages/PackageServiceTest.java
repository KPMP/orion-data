package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.externalProcess.CommandBuilder;
import org.kpmp.externalProcess.ProcessExecutor;
import org.kpmp.logging.LoggingService;
import org.kpmp.packages.state.StateHandlerService;
import org.kpmp.users.User;
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
	@Mock
	private CommandBuilder commandBuilder;
	@Mock
	private ProcessExecutor processExecutor;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new PackageService(packageFileHandler, filePathHelper, packageRepository, stateHandlerService,
				commandBuilder, processExecutor, logger);
		ReflectionTestUtils.setField(service, "uploadSucceededState", "UPLOAD_SUCCEEDED");
		ReflectionTestUtils.setField(service, "metadataReceivedState", "METADATA_RECEIVED");
	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}

	@Test
	public void testSendStateChangeEvent() throws Exception {
		service.sendStateChangeEvent("packageId", "stateString", "codicil");

		verify(stateHandlerService).sendStateChange("packageId", "stateString", "codicil");
	}

	@Test
	public void testFindAllPackages() throws JSONException, IOException {
		JSONObject uploadedPackage = mock(JSONObject.class);
		when(uploadedPackage.toString()).thenReturn("");
		when(uploadedPackage.getString("_id")).thenReturn("packageId");
		List<JSONObject> expectedResults = Arrays.asList(uploadedPackage);
		when(packageRepository.findAll()).thenReturn(expectedResults);
		when(filePathHelper.getZipFileName("packageId")).thenReturn("/data/packageId/packageId.zip");

		List<PackageView> packages = service.findAllPackages();

		assertEquals(false, packages.get(0).isDownloadable());
		verify(packageRepository).findAll();
	}

	@Test
	public void testSavePackageInformation() throws Exception {
		JSONObject packageMetadata = mock(JSONObject.class);
		User user = mock(User.class);

		String packageId = service.savePackageInformation(packageMetadata, user, "awesomeNewId");

		assertEquals("awesomeNewId", packageId);
		verify(packageRepository).saveDynamicForm(packageMetadata, user, "awesomeNewId");
		verify(stateHandlerService).sendStateChange("awesomeNewId", "METADATA_RECEIVED");
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

		service.saveFile(file, "packageId", "metadata.json", shouldAppend);

		verify(packageFileHandler).saveMultipartFile(file, "packageId", "metadata_user.json", shouldAppend);
	}

	@Test
	public void testSaveFile_whenInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		Package packageToUpdate = mock(Package.class);
		when(packageRepository.findByPackageId("packageId")).thenReturn(packageToUpdate);
		boolean shouldAppend = false;

		service.saveFile(file, "packageId", "filename", shouldAppend);

		verify(packageFileHandler).saveMultipartFile(file, "packageId", "filename", shouldAppend);
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

		service.saveFile(file, "packageId", "filename", shouldAppend);

		verify(packageFileHandler).saveMultipartFile(file, "packageId", "filename", shouldAppend);
		verify(packageRepository, times(0)).findByPackageId("packageId");
		verify(packageRepository, times(0)).save(packageToUpdate);
	}

	@Test
	public void testSaveFile_whenException() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		boolean shouldAppend = true;
		Exception expectedException = new IOException();
		doThrow(expectedException).when(packageFileHandler).saveMultipartFile(file, "packageId", "filename",
				shouldAppend);

		try {
			service.saveFile(file, "packageId", "filename", shouldAppend);
			fail("Should have thrown exception");
		} catch (Exception actual) {
			assertEquals(expectedException, actual);
		}
	}

	@Test
	public void testGetPackageFile_doesntExist() throws Exception {
		String packageId = "abc-345";
		Path packagePath = Files.createTempDirectory("data");
		packagePath.toFile().deleteOnExit();
		when(filePathHelper.getZipFileName(packageId))
				.thenReturn(packagePath.toString() + File.separator + packageId + ".zip");

		try {
			service.getPackageFile(packageId);
			fail("expected a RuntimeException");
		} catch (RuntimeException expectedException) {
			assertEquals("The file was not found: " + packageId + ".zip", expectedException.getMessage());
		}
	}

	@Test
	public void testGetPackageFile_exists() throws Exception {
		String packageId = "abc-345";
		Path packagePath = Files.createTempDirectory("data");
		packagePath.toFile().deleteOnExit();
		String expectedFilePathString = Paths.get(packagePath.toString(), packageId + ".zip").toString();
		File file = new File(expectedFilePathString);
		file.createNewFile();
		file.deleteOnExit();
		when(filePathHelper.getZipFileName(packageId)).thenReturn(expectedFilePathString);

		Path actualFilePath = service.getPackageFile(packageId);

		assertEquals(expectedFilePathString, actualFilePath.toString());
		assertTrue(actualFilePath.toFile().exists());
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
	public void testCheckFilesExistFalse() throws Exception {
		User user = mock(User.class);

		assertEquals(false, service.checkFilesExist(Arrays.asList("file1", "file2"),
				Arrays.asList("file1", "file2, file3"), "packageId", user));
		verify(logger).logErrorMessage(PackageService.class, user, "packageId", "PackageService.checkFilesExist",
				"ERROR|zip|File list in metadata does not match file list on disk");
	}

}
