package org.kpmp.packages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class AttachmentTest {

	private Attachment attachment;

	@BeforeEach
	public void setUp() throws Exception {
		attachment = new Attachment();
	}

	@AfterEach
	public void tearDown() throws Exception {
		attachment = null;
	}

	@Test
	public void testSetId() {
		attachment.setId("id");
		assertEquals("id", attachment.getId());
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
	public void testSetCheckum() {
		attachment.setMd5checksum("123978476g8fkjfsd98");
		assertEquals("123978476g8fkjfsd98", attachment.getMd5checksum());
	}

}
