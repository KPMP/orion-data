package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PackageZipServiceTest {

	@Mock
	private FilePathHelper filePathHelper;
	private PackageZipService service;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new PackageZipService(filePathHelper);
	}

	@AfterEach
	void tearDown() throws Exception {
		service = null;
	}

	@Test
	void testCreateZipFile() throws IOException {
		Path packageDirectory = Files.createTempDirectory("234");
		File attachment1Path = File.createTempFile("orion", ".txt", packageDirectory.toFile());
		Attachment attachment1 = mock(Attachment.class);
		when(attachment1.getFileName()).thenReturn(attachment1Path.getName());
		Package packageInformation = mock(Package.class);
		when(packageInformation.getPackageId()).thenReturn("234");
		when(packageInformation.getAttachments()).thenReturn(Arrays.asList(attachment1));
		when(filePathHelper.getPackagePath("234")).thenReturn(packageDirectory.toString() + File.separator);
		when(filePathHelper.getZipFileName("234")).thenReturn(packageDirectory.toString() + File.separator + "234.zip");

		service.createZipFile(packageInformation);

		File zipFile = new File(packageDirectory.toString() + File.separator + "234.zip");
		assertEquals(true, zipFile.exists());
		ZipFile zip = new ZipFile(packageDirectory.toString() + File.separator + "234.zip");
		Enumeration<? extends ZipEntry> entries = zip.entries();
		int count = 0;
		while (entries.hasMoreElements()) {
			count++;
			ZipEntry entry = entries.nextElement();
			assertEquals(attachment1Path.getName(), entry.getName());
		}

		assertEquals(1, count);
	}

}
