package org.kpmp.shibboleth;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AtttributeControllerTest extends AtttributeController {

	private AtttributeController controller;

	@Before
	public void setUp() throws Exception {
		controller = new AtttributeController();
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testGetDisplayName() throws UnsupportedEncodingException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getHeader("displayname")).thenReturn("Johnny Cash");

		String displayName = controller.getDisplayName(request);

		assertEquals("Johnny Cash", displayName);

	}

}
