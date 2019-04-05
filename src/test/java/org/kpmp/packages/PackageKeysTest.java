package org.kpmp.packages;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PackageKeysTest {

	@Test
	public void testLength() throws Exception {
		assertEquals(18, PackageKeys.values().length);
	}

	@Test
	public void testGetKey() {
		assertEquals("_class", PackageKeys.CLASS.getKey());
		assertEquals("createdAt", PackageKeys.CREATED_AT.getKey());
		assertEquals("displayName", PackageKeys.DISPLAY_NAME.getKey());
		assertEquals("email", PackageKeys.EMAIL.getKey());
		assertEquals("files", PackageKeys.FILES.getKey());
		assertEquals("fileName", PackageKeys.FILE_NAME.getKey());
		assertEquals("firstName", PackageKeys.FIRST_NAME.getKey());
		assertEquals("_id", PackageKeys.ID.getKey());
		assertEquals("lastName", PackageKeys.LAST_NAME.getKey());
		assertEquals("regenerateZip", PackageKeys.REGENERATE_ZIP.getKey());
		assertEquals("size", PackageKeys.SIZE.getKey());
		assertEquals("submitter", PackageKeys.SUBMITTER.getKey());
		assertEquals("submitterEmail", PackageKeys.SUBMITTER_EMAIL.getKey());
		assertEquals("submitterFirstName", PackageKeys.SUBMITTER_FIRST_NAME.getKey());
		assertEquals("$oid", PackageKeys.SUBMITTER_ID.getKey());
		assertEquals("$id", PackageKeys.SUBMITTER_ID_OBJECT.getKey());
		assertEquals("submitterLastName", PackageKeys.SUBMITTER_LAST_NAME.getKey());
		assertEquals("version", PackageKeys.VERSION.getKey());
	}

}
