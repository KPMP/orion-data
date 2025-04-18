package org.miktmc.packages;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

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
	public void testSetReplacedOn() {
		Date now = new Date();
		attachment.setReplacedOn(now);
		assertEquals(now, attachment.getReplacedOn());
	}

	@Test
	public void testSetValidated() {
		assertFalse(attachment.getValidated());
		attachment.setValidated(true);
		assertTrue(attachment.getValidated());
	}

}
