package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.UniversalIdGenerator;
import org.kpmp.users.User;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class PackageControllerTest {

	@Mock
	private PackageService packageService;
	@Mock
	private UniversalIdGenerator idGenerator;
	private PackageController controller;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new PackageController(packageService, idGenerator);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testGetAllPackages() {
		List<PackageView> expectedPackages = Arrays.asList(new PackageView(new Package()));
		when(packageService.findAllPackages()).thenReturn(expectedPackages);

		List<PackageView> packages = controller.getAllPackages();

		assertEquals(expectedPackages, packages);
		verify(packageService).findAllPackages();
	}

	@Test
	public void testPostPackageInfo() throws Exception {
		Package packageInfo = mock(Package.class);
		Package savedPackage = mock(Package.class);
		when(savedPackage.getPackageId()).thenReturn("universalId");
		User user = new User();
		user.setId("1234");
		user.setEmail("emailaddress");
		when(packageInfo.getSubmitter()).thenReturn(user);
		when(idGenerator.generateUniversalId()).thenReturn("universalId");
		when(packageService.savePackageInformation(packageInfo)).thenReturn(savedPackage);

		String universalId = controller.postPackageInfo(packageInfo);

		assertEquals("universalId", universalId);
		verify(packageService).savePackageInformation(packageInfo);
		verify(packageInfo).setPackageId("universalId");
		verify(packageInfo).setCreatedAt(any(Date.class));
	}

	@Test
	public void testPostPackageInfo_logsTimingCorrectly() throws Exception {
		Logger testLogger = (Logger) LoggerFactory.getLogger(PackageController.class);
		ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
		listAppender.start();
		testLogger.addAppender(listAppender);
		when(idGenerator.generateUniversalId()).thenReturn("universalId");
		Package packageInfo = new Package();
		User user = new User();
		user.setEmail("emailaddress");
		packageInfo.setSubmitter(user);
		packageInfo.setAttachments(Arrays.asList(new Attachment(), new Attachment()));
		when(packageService.savePackageInformation(packageInfo)).thenReturn(packageInfo);

		controller.postPackageInfo(packageInfo);

		List<ILoggingEvent> logsList = listAppender.list;
		assertEquals(true, logsList.get(0).getMessage().startsWith("Request|postPackageInfo"));
		String timingMessage = logsList.get(1).getMessage();
		assertEquals(true, timingMessage.startsWith("Timing|start|"));
		assertEquals(true, timingMessage.contains("|emailaddress|"));
		assertEquals(true, timingMessage.contains("|universalId|"));
		assertEquals(true, timingMessage.contains("|2 files"));
	}

	@Test
	public void testPostFilesToPackage_whenNotInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 2);

		verify(packageService).saveFile(file, "packageId", "filename", true);
	}

	@Test
	public void testPostFilesToPackage_whenInitialChunk() throws Exception {
		MultipartFile file = mock(MultipartFile.class);
		controller.postFilesToPackage("packageId", file, "filename", 1234, 3, 0);

		verify(packageService).saveFile(file, "packageId", "filename", false);
	}

	@Test
	public void testFinishUpload() throws Exception {
		Package packageInfo = mock(Package.class);
		when(packageInfo.getSubmitter()).thenReturn(mock(User.class));
		when(packageService.findPackage("3545")).thenReturn(packageInfo);
		when(packageInfo.getCreatedAt()).thenReturn(new Date());

		controller.finishUpload("3545");

		verify(packageService).createZipFile("3545");
	}

	@Test
	public void testFinishUpload_logsUploadTimingCorrectly() throws Exception {
		Logger testLogger = (Logger) LoggerFactory.getLogger(PackageController.class);
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
		when(packageService.findPackage("123")).thenReturn(packageInfo);

		controller.finishUpload("123");

		List<ILoggingEvent> logsList = listAppender.list;
		assertEquals(true, logsList.get(0).getMessage().startsWith("Request|finishUpload"));
		String timingMessage = logsList.get(1).getMessage();
		assertEquals(true, timingMessage.startsWith("Timing|end|"));
		assertEquals(true, timingMessage.contains("|emailAddress|"));
		assertEquals(true, timingMessage.contains("|123|"));
		assertEquals(true, timingMessage.contains("|2 files|"));
		String displaySize = FileUtils.byteCountToDisplaySize(55555555 + 6666);
		assertEquals(true, timingMessage.contains("|" + displaySize + "|"));
		assertEquals(true, timingMessage.contains("|10 seconds|"));
		assertEquals(true, timingMessage.contains("|5.299 MB/sec"));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testFinishUpload_logsZipTimingCorrectly() throws Exception {
		Logger testLogger = (Logger) LoggerFactory.getLogger(PackageController.class);
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
		when(packageService.findPackage("123")).thenReturn(packageInfo);
		doAnswer((Answer) invocation -> {
			TimeUnit.MILLISECONDS.sleep(5000);
			return null;
		}).when(packageService).createZipFile("123");

		controller.finishUpload("123");

		List<ILoggingEvent> logsList = listAppender.list;
		assertEquals(true, logsList.get(0).getMessage().startsWith("Request|finishUpload|"));
		assertEquals(true, logsList.get(1).getMessage().startsWith("Timing|end|"));
		String timingMessage = logsList.get(2).getMessage();
		assertEquals(true, timingMessage.startsWith("Timing|zip"));
		assertEquals(true, timingMessage.contains("|emailAddress|"));
		assertEquals(true, timingMessage.contains("|123|"));
		assertEquals(true, timingMessage.contains("|2 files|"));
		String displaySize = FileUtils.byteCountToDisplaySize(55555555 + 6666);
		assertEquals(true, timingMessage.contains("|" + displaySize + "|"));
		assertEquals(true, timingMessage.contains("|5 seconds"));
	}

	@Test
	public void testDownloadPackage() throws Exception {
		String packageId = "1234";
		when(packageService.getPackageFile(packageId)).thenReturn(Paths.get("foo", "1234.zip"));

		ResponseEntity<Resource> response = controller.downloadPackage(packageId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).iterator().next().contains("1234.zip"));
	}

	@Test
	public void testDownloadPackage_serviceException() throws Exception {
		String packageId = "1234";
		when(packageService.getPackageFile(packageId)).thenThrow(new RuntimeException("angry"));

		try {
			controller.downloadPackage(packageId);
			fail("expected RuntimeException");
		} catch (RuntimeException expectedException) {
			assertEquals("angry", expectedException.getMessage());
		}
	}

}
