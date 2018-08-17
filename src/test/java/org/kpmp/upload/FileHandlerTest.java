package org.kpmp.upload;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileHandlerTest {

	private FileHandler fileHandler;
	@Mock
	private FilePathHelper filePathHelper;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		fileHandler = new FileHandler(filePathHelper);
		ReflectionTestUtils.setField(fileHandler, "basePath", "/data");
	}

	@After
	public void tearDown() throws Exception {
		fileHandler = null;
	}

	@Test
	public void testSaveMultipartFile_fullFile() throws IllegalStateException, IOException {
		// We acknowledge that we are not testing that this code is saving the
		// file/creating missing directories, etc
		// this is more of an integration level test. We will cover this in an
		// integration test at a later point

		when(filePathHelper.getMetadataFileName()).thenReturn("metadata.json");
		MultipartFile file = mock(MultipartFile.class);
		InputStream testInputStream = IOUtils.toInputStream("here is some test data", "UTF-8");
		when(file.getInputStream()).thenReturn(testInputStream);

		File savedFile = fileHandler.saveMultipartFile(file, 4, "filename.txt", true);
		File expectedFile = new File("/data/package4/filename.txt");

		assertEquals(expectedFile.getPath(), savedFile.getPath());
	}

	@Test
	public void testFileNameSameAsMetadata() throws IllegalStateException, IOException {

		when(filePathHelper.getMetadataFileName()).thenReturn("metadata.json");
		MultipartFile file = mock(MultipartFile.class);
		InputStream testInputStream = IOUtils.toInputStream("here is some test data", "UTF-8");
		when(file.getInputStream()).thenReturn(testInputStream);

		File savedFile = fileHandler.saveMultipartFile(file, 4, "metadata.json", true);
		File expectedFile = new File("/data/package4/metadata_user.json");

		assertEquals(expectedFile.getPath(), savedFile.getPath());
	}

	@Test
	public void testFileNameSameAsMetadataUppercase() throws IllegalStateException, IOException {

		when(filePathHelper.getMetadataFileName()).thenReturn("metadata.json");
		MultipartFile file = mock(MultipartFile.class);
		InputStream testInputStream = IOUtils.toInputStream("here is some test data", "UTF-8");
		when(file.getInputStream()).thenReturn(testInputStream);

		File savedFile = fileHandler.saveMultipartFile(file, 4, "METADATA.JSON", true);
		File expectedFile = new File("/data/package4/METADATA_user.JSON");

		assertEquals(expectedFile.getPath(), savedFile.getPath());
	}

	@Test
	public void testSaveMultipartFile_partialFile() throws IllegalStateException, IOException {
		// We acknowledge that we are not testing that this code is saving the
		// file/creating missing directories, etc
		// this is more of an integration level test. We will cover this in an
		// integration test at a later point

		MultipartFile file = mock(MultipartFile.class);
		InputStream testInputStream = IOUtils.toInputStream("here is some test data", "UTF-8");
		when(file.getInputStream()).thenReturn(testInputStream);
		
		File savedFile = fileHandler.saveMultipartFile(file, 4, "filename.txt", false);
		File expectedFile = new File("/data/package4/filename.txt");

		assertEquals(expectedFile.getPath(), savedFile.getPath());
	}

}
