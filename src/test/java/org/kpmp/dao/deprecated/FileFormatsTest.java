package org.kpmp.dao.deprecated;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.dao.deprecated.FileFormats;
import org.kpmp.dao.deprecated.FileSubmission;

public class FileFormatsTest {

	private FileFormats fileFormat;

	@Before
	public void setUp() throws Exception {
		fileFormat = new FileFormats();
	}

	@After
	public void tearDown() throws Exception {
		fileFormat = null;
	}

	@Test
	public void testSetFormatType() {
		fileFormat.setFormatType("png");
		assertEquals("png", fileFormat.getFormatType());
	}

	@Test
	public void testSetId() {
		fileFormat.setId(5);
		assertEquals(5, fileFormat.getId());
	}

	@Test
	public void testSetFileSubmissions() throws Exception {
		List<FileSubmission> fileSubmissions = Arrays.asList(new FileSubmission());
		fileFormat.setFileSubmissions(fileSubmissions);
		assertEquals(fileSubmissions, fileFormat.getFileSubmissions());
	}

}