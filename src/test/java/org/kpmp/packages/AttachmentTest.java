package org.kpmp.packages;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AttachmentTest {

	private Attachment attachment;

	@Before
	public void setUp() throws Exception {
		attachment = new Attachment();
	}

	@After
	public void tearDown() throws Exception {
		attachment = null;
	}

	@Test
	public void testSetId() {
		attachment.setId("id");
		assertEquals("id", attachment.getId());
	}

	@Test
	public void testSetPath() {
		attachment.setPath("path/to/file");
		assertEquals("path/to/file", attachment.getPath());
	}

	@Test
	public void testSetSize() {
		attachment.setSize(344);
		assertEquals(344, attachment.getSize());
	}

	@Test
	public void testSetFilename() {
		attachment.setFileName("filename.txt");
		assertEquals("filename.txt", attachment.getFileName());
	}

	@Test
	public void testSetDescription() {
		attachment.setDescription("description");
		assertEquals("description", attachment.getDescription());
	}

}
