package org.kpmp.packages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

public class PackageFileHandlerTest {

	@Mock
	private FilePathHelper filePathHelper;
	private PackageFileHandler fileHandler;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		fileHandler = new PackageFileHandler(filePathHelper);
	}

	@After
	public void tearDown() throws Exception {
		fileHandler = null;
	}



	@Test
	public void testSaveMultipartFile_firstPart() throws IOException {
		Path dataDirectory = Files.createTempDirectory("packageFileHandler");
		File tempDirectory = new File(dataDirectory.toString());
		tempDirectory.deleteOnExit();
		String dataDirectoryPath = dataDirectory.toString();
		when(filePathHelper.getPackagePath("packageId")).thenReturn(dataDirectoryPath);
		MultipartFile file = mock(MultipartFile.class);
		InputStream testInputStream = IOUtils.toInputStream("Here is the data in the file", "UTF-8");
		when(file.getInputStream()).thenReturn(testInputStream);

		fileHandler.saveMultipartFile(file, "packageId", "filename.txt", false);

		File savedFile = new File(dataDirectoryPath + File.separator + "filename.txt");
		assertEquals(true, savedFile.exists());
		BufferedReader reader = new BufferedReader(new FileReader(savedFile));
		String line = "";
		String fileContents = "";
		while ((line = reader.readLine()) != null) {
			fileContents += line;
		}
		assertEquals("Here is the data in the file", fileContents);
		reader.close();
	}

	@Test
	public void testSaveMultipartFile_createsMissingDirectories() throws IOException {
		Path dataDirectory = Files.createTempDirectory("packageFileHandler");
		File tempDirectory = new File(dataDirectory.toString());
		tempDirectory.deleteOnExit();
		String dataDirectoryPath = dataDirectory.toString() + File.separator + "anotherDirectory";
		when(filePathHelper.getPackagePath("packageId")).thenReturn(dataDirectoryPath);
		MultipartFile file = mock(MultipartFile.class);
		InputStream testInputStream = IOUtils.toInputStream("Here is the data in the file", "UTF-8");
		when(file.getInputStream()).thenReturn(testInputStream);

		fileHandler.saveMultipartFile(file, "packageId", "filename.txt", false);

		File savedFile = new File(dataDirectoryPath + File.separator + "filename.txt");
		assertEquals(true, savedFile.exists());
	}

	@Test
	public void testSaveMultipartFile_twoParts() throws IOException {
		Path dataDirectory = Files.createTempDirectory("packageFileHandler");
		File tempDirectory = new File(dataDirectory.toString());
		tempDirectory.deleteOnExit();
		String dataDirectoryPath = dataDirectory.toString();
		when(filePathHelper.getPackagePath("packageId")).thenReturn(dataDirectoryPath);
		MultipartFile filePartOne = mock(MultipartFile.class);
		InputStream testInputStream1 = IOUtils.toInputStream("Here is the data in the file", "UTF-8");
		when(filePartOne.getInputStream()).thenReturn(testInputStream1);
		MultipartFile filePartTwo = mock(MultipartFile.class);
		InputStream testInputStream2 = IOUtils.toInputStream("Here is the more data", "UTF-8");
		when(filePartTwo.getInputStream()).thenReturn(testInputStream2);

		fileHandler.saveMultipartFile(filePartOne, "packageId", "filename.txt", false);
		File firstPart = new File(dataDirectoryPath + File.separator + "filename.txt");
		long firstPartFileSize = firstPart.length();
		fileHandler.saveMultipartFile(filePartTwo, "packageId", "filename.txt", true);

		File savedFile = new File(dataDirectoryPath + File.separator + "filename.txt");
		assertEquals(true, firstPartFileSize < savedFile.length());
		assertEquals(true, savedFile.exists());
		BufferedReader reader = new BufferedReader(new FileReader(savedFile));
		String line = "";
		String fileContents = "";
		while ((line = reader.readLine()) != null) {
			fileContents += line;
		}
		assertEquals("Here is the data in the fileHere is the more data", fileContents);
		reader.close();
	}

	@Test
	public void testSaveMultipartFile_fileExists() throws IOException {
		Path dataDirectory = Files.createTempDirectory("packageFileHandler");
		File tempDirectory = new File(dataDirectory.toString());
		tempDirectory.deleteOnExit();
		String dataDirectoryPath = dataDirectory.toString();
		when(filePathHelper.getPackagePath("packageId")).thenReturn(dataDirectoryPath);
		MultipartFile fileOne = mock(MultipartFile.class);
		InputStream testInputStream1 = IOUtils.toInputStream("Here is the data in file 1", "UTF-8");
		when(fileOne.getInputStream()).thenReturn(testInputStream1);
		MultipartFile fileTwo = mock(MultipartFile.class);
		InputStream testInputStream2 = IOUtils.toInputStream("Here is the data in file 2", "UTF-8");
		when(fileTwo.getInputStream()).thenReturn(testInputStream2);

		try {
			fileHandler.saveMultipartFile(fileOne, "packageId", "filename.txt", false);
			fileHandler.saveMultipartFile(fileOne, "packageId", "filename.txt", false);
			fail("Should have thrown exception");
		} catch (FileAlreadyExistsException e) {

		}

	}

}
