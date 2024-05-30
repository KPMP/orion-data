package org.kpmp.globus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class GlobusListingResponseTest {

	private GlobusListingResponse response;

	@BeforeEach
	public void setUp() throws Exception {
		response = new GlobusListingResponse();
	}

	@AfterEach
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
