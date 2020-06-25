package org.kpmp.globus;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GlobusFileListingTest {

	private GlobusFileListing listing;

	@Before
	public void setUp() throws Exception {
		listing = new GlobusFileListing();
	}

	@After
	public void tearDown() throws Exception {
		listing = null;
	}

	@Test
	public void testSetDataType() {
		listing.setDataType("stuff");
		assertEquals("stuff", listing.getDataType());
	}

	@Test
	public void testSetName() {
		listing.setName("filename");
		assertEquals("filename", listing.getName());
	}

	@Test
	public void testSetType() {
		listing.setType("type");
		assertEquals("type", listing.getType());
	}

	@Test
	public void testSetSize() {
		listing.setSize(34345l);
		assertEquals(34345l, listing.getSize());
	}

}
