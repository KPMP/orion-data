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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.users.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
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
	private PackageZipService packageZipService;
	@Mock
	private FilePathHelper filePathHelper;
	private PackageService service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new PackageService(packageFileHandler, packageZipService, filePathHelper, packageRepository);
	}

	@After
	public void tearDown() throws Exception {
		service = null;
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
		when(packageRepository.saveDynamicForm(packageMetadata)).thenReturn("awesomeNewId");

		String packageId = service.savePackageInformation(packageMetadata);

		assertEquals("awesomeNewId", packageId);
		verify(packageRepository).saveDynamicForm(packageMetadata);
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

		service.saveFile(file, "packageId", "metadata.json", file.getSize(), shouldAppend);

		verify(packageFileHandler).saveMultipartFile(file, "packageId", "metadata_user.json", shouldAppend);
	}

	@Test
	public void testSaveFile_whenInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		Package packageToUpdate = mock(Package.class);
		when(packageRepository.findByPackageId("packageId")).thenReturn(packageToUpdate);
		boolean shouldAppend = false;

		service.saveFile(file, "packageId", "filename", file.getSize(), shouldAppend);

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

		service.saveFile(file, "packageId", "filename", file.getSize(), shouldAppend);

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
			service.saveFile(file, "packageId", "filename", file.getSize(), shouldAppend);
			fail("Should have thrown exception");
		} catch (Exception actual) {
			assertEquals(expectedException, actual);
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Test
	public void testSaveFile_whenFileIncorrectSizeException() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		Package packageToUpdate = mock(Package.class);
		when(packageRepository.findByPackageId("packageId")).thenReturn(packageToUpdate);
		ArrayList<Attachment> files = mock(ArrayList.class);
		when(packageToUpdate.getAttachments()).thenReturn(files);
		boolean shouldAppend = true;
		long expectedSize = file.getSize() + 100;
		Exception expectedException = new RuntimeException();
		doThrow(expectedException).when(packageFileHandler).assertPackageFileHasSize("packageId", "filename",
				expectedSize);

		try {
			service.saveFile(file, "packageId", "filename", expectedSize, shouldAppend);
			fail("Should have failed test file size " + file.getSize() + " with expected size " + expectedSize);
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

	@Test
	public void testCreateZipFile_logsUploadTimingCorrectly() throws Exception {
		Logger testLogger = (Logger) LoggerFactory.getLogger(PackageService.class);
		ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
		listAppender.start();
		testLogger.addAppender(listAppender);
		Package packageInfo = mock(Package.class);
		User user = mock(User.class);
		when(user.getEmail()).thenReturn("emailAddress");
		when(packageInfo.getSubmitter()).thenReturn(user);
		Attachment attachment1 = new Attachment();
		attachment1.setSize(55555555);
		Attachment attachment2 = new Attachment();
		attachment2.setSize(6666);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, -10);
		when(packageInfo.getCreatedAt()).thenReturn(new Date(calendar.getTimeInMillis()));
		when(packageInfo.getAttachments()).thenReturn(Arrays.asList(attachment1, attachment2));
		when(packageRepository.findByPackageId("123")).thenReturn(packageInfo);

		service.createZipFile("123");

		List<ILoggingEvent> logsList = listAppender.list;
		String timingMessage = logsList.get(0).getMessage();
		assertEquals(true, timingMessage.startsWith("Timing|end|"));
		assertEquals(true, timingMessage.contains("|emailAddress|"));
		assertEquals(true, timingMessage.contains("|123|"));
		assertEquals(true, timingMessage.contains("|2 files|"));
		String displaySize = FileUtils.byteCountToDisplaySize(55555555 + 6666);
		assertEquals(true, timingMessage.contains("|" + displaySize + "|"));
		assertEquals(true, timingMessage.contains("|10 seconds|"));
		assertEquals(true, timingMessage.contains("|5.299 MB/sec"));
	}

	@Test
	public void testCheckFilesExistTrue() throws Exception {
		when(packageRepository.findByPackageId("123")).thenReturn(mock(Package.class));
		Attachment attachment1 = new Attachment();
		attachment1.setFileName("file1");
		Attachment attachment2 = new Attachment();
		attachment2.setFileName("file2");
		when(filePathHelper.getPackagePath("123")).thenReturn("path");
		when(filePathHelper.getFilenames("path")).thenReturn(Arrays.asList("file2", "file1"));

		assertEquals(true, service.checkFilesExist("123"));
	}

	@Test
	public void testCheckFilesExistFalse() throws Exception {
		Package packageInfo = new Package();
		when(packageRepository.findByPackageId("123")).thenReturn(packageInfo);
		Attachment attachment1 = new Attachment();
		attachment1.setFileName("file1");
		Attachment attachment2 = new Attachment();
		attachment2.setFileName("file2");
		packageInfo.setAttachments(Arrays.asList(attachment1, attachment2));
		when(filePathHelper.getPackagePath("123")).thenReturn("path");
		when(filePathHelper.getFilenames("path")).thenReturn(Arrays.asList("file1"));
		assertEquals(false, service.checkFilesExist("123"));
	}

}
