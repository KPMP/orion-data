package org.kpmp.externalProcess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.packages.FilePathHelper;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CommandBuilderTest {

	@Mock
	private FilePathHelper filePathHelper;
	private CommandBuilder builder;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		builder = new CommandBuilder(filePathHelper);
	}

	@After
	public void tearDown() throws Exception {
		builder = null;
	}

	@Test
	public void testBuildZipCommand_oneFile() {
		when(filePathHelper.getPackagePath("packageId")).thenReturn("/here/is/a/path");
		when(filePathHelper.getFilenames("/here/is/a/path")).thenReturn(Arrays.asList("file1.txt"));
		when(filePathHelper.getZipFileName("packageId")).thenReturn("/here/is/a/path/packageId.zip");

		String[] command = builder.buildZipCommand("packageId", "metadata contents");

		assertEquals(6, command.length);
		assertEquals("java", command[0]);
		assertEquals("-jar", command[1]);
		assertEquals("/home/gradle/zipWorker/zipWorker.jar", command[2]);
		assertEquals("--zip.fileNames=/here/is/a/path/file1.txt", command[3]);
		assertEquals("--zip.zipFilePath=/here/is/a/path/packageId.zip", command[4]);
		assertEquals("--zip.additionalFileData=metadata.json|metadata contents", command[5]);
	}

	@Test
	public void testBuildZipCommand_multipleFiles() {
		when(filePathHelper.getPackagePath("packageId")).thenReturn("/here/is/a/path");
		when(filePathHelper.getFilenames("/here/is/a/path")).thenReturn(Arrays.asList("file1.txt", "file2.txt"));
		when(filePathHelper.getZipFileName("packageId")).thenReturn("/here/is/a/path/packageId.zip");

		String[] command = builder.buildZipCommand("packageId", "metadata contents with a /");

		assertEquals(7, command.length);
		assertEquals("java", command[0]);
		assertEquals("-jar", command[1]);
		assertEquals("/home/gradle/zipWorker/zipWorker.jar", command[2]);
		assertEquals("--zip.fileNames=/here/is/a/path/file1.txt", command[3]);
		assertEquals("--zip.fileNames=/here/is/a/path/file2.txt", command[4]);
		assertEquals("--zip.zipFilePath=/here/is/a/path/packageId.zip", command[5]);
		assertEquals("--zip.additionalFileData=metadata.json|metadata contents with a /", command[6]);
	}

}
