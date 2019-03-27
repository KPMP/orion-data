package org.kpmp.packages;

import static org.junit.Assert.assertEquals;

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
	public void testSetIsDownloadable() {
		packageView.setIsDownloadable(true);
		assertEquals(true, packageView.isDownloadable());
	}

	@Test
	public void testSetPackageJSON() throws IOException {
		JSONObject packageInfo = new JSONObject();
		ObjectMapper mapper = new ObjectMapper();
		packageView.setPackageInfo(packageInfo);

		assertEquals(mapper.readTree(packageInfo.toString()), packageView.getPackageInfo());
	}

}
