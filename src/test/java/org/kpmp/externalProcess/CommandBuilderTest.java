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
		when(filePathHelper.getFilenames("/here/is/a/path")).thenReturn(Arrays.asList("/here/is/a/path/file1.txt"));
		when(filePathHelper.getZipFileName("packageId")).thenReturn("/here/is/a/path/packageId.zip");

		String command = builder.buildZipCommand("packageId", "metadata contents");

		assertEquals(
				"java -jar zipWorker.jar --zip.fileNames=/here/is/a/path/file1.txt --zip.zipFilePath=/here/is/a/path/packageId.zip --zip.additionalFileData=\"metadata.json|metadata contents\"",
				command);
	}

	@Test
	public void testBuildZipCommand_multipleFiles() {
		when(filePathHelper.getPackagePath("packageId")).thenReturn("/here/is/a/path");
		when(filePathHelper.getFilenames("/here/is/a/path"))
				.thenReturn(Arrays.asList("/here/is/a/path/file1.txt", "/here/is/a/path/file2.txt"));
		when(filePathHelper.getZipFileName("packageId")).thenReturn("/here/is/a/path/packageId.zip");

		String command = builder.buildZipCommand("packageId", "metadata contents");

		assertEquals(
				"java -jar zipWorker.jar --zip.fileNames=/here/is/a/path/file1.txt --zip.fileNames=/here/is/a/path/file2.txt "
						+ "--zip.zipFilePath=/here/is/a/path/packageId.zip --zip.additionalFileData=\"metadata.json|metadata contents\"",
				command);
	}

}
