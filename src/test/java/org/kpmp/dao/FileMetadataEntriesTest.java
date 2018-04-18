package org.kpmp.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileMetadataEntriesTest {

	private FileMetadataEntries fileMetadata;

	@Before
	public void setUp() throws Exception {
		fileMetadata = new FileMetadataEntries();
	}

	@After
	public void tearDown() throws Exception {
		fileMetadata = null;
	}

	@Test
	public void testSetId() {
		fileMetadata.setId(5);
		assertEquals(5, fileMetadata.getId());
	}

	@Test
	public void testSetMetadata() {
		fileMetadata.setMetadata("metadata");
		assertEquals("metadata", fileMetadata.getMetadata());
	}

	@Test
	public void testSetDeletedAt() {
		Date deletedAt = new Date();
		fileMetadata.setDeletedAt(deletedAt);
		assertEquals(deletedAt, fileMetadata.getDeletedAt());
	}

	@Test
	public void testSetCreatedAt() {
		Date createdAt = new Date();
		fileMetadata.setCreatedAt(createdAt);
		assertEquals(createdAt, fileMetadata.getCreatedAt());
	}

	@Test
	public void testSetUpdatedAt() {
		Date updatedAt = new Date();
		fileMetadata.setUpdatedAt(updatedAt);
		assertEquals(updatedAt, fileMetadata.getUpdatedAt());
	}

}
