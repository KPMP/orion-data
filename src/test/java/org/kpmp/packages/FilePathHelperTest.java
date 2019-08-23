package org.kpmp.packages;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class FilePathHelperTest {

	private FilePathHelper filePathHelper;

	@Before
	public void setUp() throws Exception {
		filePathHelper = new FilePathHelper();
		ReflectionTestUtils.setField(filePathHelper, "basePath", File.separator + "data");
	}

	@After
	public void tearDown() throws Exception {
		filePathHelper = null;
	}

	@Test
	public void testGetPackagePath_OnlySuffix() throws Exception {
		assertEquals(File.separator + "data" + File.separator + "package_1234_UUID" + File.separator,
				filePathHelper.getPackagePath("1234_UUID"));
	}

	@Test
	public void testGetZipFileName() throws Exception {
		assertEquals(File.separator + "data" + File.separator + "package_223" + File.separator + "223.zip",
				filePathHelper.getZipFileName("223"));
	}

	@Test
	public void testGetFilenames() throws Exception {
		Path packagePath = Files.createTempDirectory("data");
		packagePath.toFile().deleteOnExit();
		String filePathString = Paths.get(packagePath.toString(), "aPackage" + ".zip").toString();
		File file = new File(filePathString);
		file.createNewFile();
		file.deleteOnExit();
		List<String> fileNames = Arrays.asList(new String[] { "aPackage.zip" });
		assertEquals(fileNames, filePathHelper.getFilenames(packagePath.toString()));
	}
}
