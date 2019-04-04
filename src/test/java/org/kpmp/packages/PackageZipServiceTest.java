package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PackageZipServiceTest {

	@Mock
	private FilePathHelper filePathHelper;
	private PackageZipService service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		service = new PackageZipService(filePathHelper);
	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}

	@Test
	public void testCreateZipFile() throws IOException, JSONException {
		Path packageDirectory = Files.createTempDirectory("234");
		File attachment1Path = File.createTempFile("orion", ".txt", packageDirectory.toFile());
		attachment1Path.deleteOnExit();
		when(filePathHelper.getPackagePath("234")).thenReturn(packageDirectory.toString() + File.separator);
		when(filePathHelper.getZipFileName("234")).thenReturn(packageDirectory.toString() + File.separator + "234.zip");

		service.createZipFile("{ \"_id\": \"234\", \"files\": [ { \"fileName\": \"" + attachment1Path.getName()
				+ "\", \"size\": 123 }]}");

		File zipFile = new File(packageDirectory.toString() + File.separator + "234.zip");
		assertEquals(true, zipFile.exists());
		ZipFile zip = new ZipFile(packageDirectory.toString() + File.separator + "234.zip");
		assertEquals(2, zip.size());
		Enumeration<? extends ZipEntry> entries = zip.entries();
		List<String> filenames = new ArrayList<>();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			filenames.add(entry.getName());
		}

		assertEquals(true, filenames.contains("metadata.json"));
		assertEquals(true, filenames.contains(attachment1Path.getName()));
		zip.close();
	}

	@Test
	public void testFormatPackageMetadata() throws Exception {
		assertEquals("One/Two/Three", service.formatPackageMetadata("One\\/Two\\/Three"));
	}

}
