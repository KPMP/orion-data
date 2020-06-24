package org.kpmp.globus;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GlobusListingResponseTest {

	private GlobusListingResponse response;

	@Before
	public void setUp() throws Exception {
		response = new GlobusListingResponse();
	}

	@After
	public void tearDown() throws Exception {
		response = null;
	}

	@Test
	public void testSetData() throws Exception {
		List<GlobusFileListing> expected = Arrays.asList(mock(GlobusFileListing.class));
		response.setData(expected);

		assertEquals(expected, response.getData());
	}
}
