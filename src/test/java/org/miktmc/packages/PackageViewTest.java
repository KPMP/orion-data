package org.miktmc.packages;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PackageViewTest {

	private PackageView packageView;

	@Before
	public void setUp() throws Exception {
		packageView = new PackageView(new JSONObject());
	}

	@After
	public void tearDown() throws Exception {
		packageView = null;
	}

	@Test
	public void testSetPackageJSON() throws IOException {
		JSONObject packageInfo = new JSONObject();
		packageInfo.put("modifications", "mods");
		packageInfo.put("modifiedBy", "modified by");
		ObjectMapper mapper = new ObjectMapper();
		assertTrue(packageInfo.has("modifications"));
		assertTrue(packageInfo.has("modifiedBy"));
		packageView.setPackageInfo(packageInfo);
		assertFalse(packageInfo.has("modifications"));
		assertFalse(packageInfo.has("modifiedBy"));
		assertEquals(mapper.readTree(packageInfo.toString()), packageView.getPackageInfo());
	}

	@Test
	public void testSetState() throws Exception {
		State newState = mock(State.class);

		packageView.setState(newState);

		assertEquals(newState, packageView.getState());
	}

}
