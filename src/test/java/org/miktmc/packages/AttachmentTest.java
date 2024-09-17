package org.miktmc.packages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

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
	public void testFilemame() {
		attachment.setFileName("file22.txt");
		assertEquals("file22.txt", attachment.getFileName());
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
	public void testSetOriginalFilename() {
		attachment.setOriginalFileName("filename.txt");
		assertEquals("filename.txt", attachment.getOriginalFileName());
	}

	@Test
	public void testSetCheckum() {
		attachment.setMd5checksum("123978476g8fkjfsd98");
		assertEquals("123978476g8fkjfsd98", attachment.getMd5checksum());
	}

	@Test
	public void testSetReplacedOn() {
		Date now = new Date();
		attachment.setReplacedOn(now);
		assertEquals(now, attachment.getReplacedOn());
	}

}
