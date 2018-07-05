package org.kpmp.upload;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileHandlerTest {

	private FileHandler fileHandler;
	private FilePathHelper filePathHelper;

	@Before
	public void setUp() throws Exception {
		filePathHelper = mock(FilePathHelper.class);
		ReflectionTestUtils.setField(filePathHelper, "basePath", File.separator + "data");
		when(filePathHelper.getPackagePath("", "1234_UUID")).thenReturn("/data/package_1234_UUID/");
		fileHandler = new FileHandler(filePathHelper);
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

		MultipartFile file = mock(MultipartFile.class);
		when(file.getBytes()).thenReturn(new byte[4]);

		File savedFile = fileHandler.saveMultipartFile(file, "1234_UUID", "filename.txt", true);

		assertEquals(File.separator + "data" + File.separator + "package_1234_UUID" + File.separator + "filename.txt", savedFile.getPath());
	}

	@Test
	public void testSaveMultipartFile_partialFile() throws IllegalStateException, IOException {
		// We acknowledge that we are not testing that this code is saving the
		// file/creating missing directories, etc
		// this is more of an integration level test. We will cover this in an
		// integration test at a later point

		MultipartFile file = mock(MultipartFile.class);
		when(file.getBytes()).thenReturn(new byte[4]);

		File savedFile = fileHandler.saveMultipartFile(file, "1234_UUID", "filename.txt", false);

		assertEquals(File.separator + "data" + File.separator + "package_1234_UUID" + File.separator + "filename.txt", savedFile.getPath());
	}

}
