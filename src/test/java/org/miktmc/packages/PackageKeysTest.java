package org.miktmc.packages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PackageKeysTest {

	@Test
	public void testLength() throws Exception {
		assertEquals(30, PackageKeys.values().length);
	}

	@Test
	public void testGetKey() {
		assertEquals("biopsyId", PackageKeys.BIOPSY_ID.getKey());
		assertEquals("_class", PackageKeys.CLASS.getKey());
		assertEquals("createdAt", PackageKeys.CREATED_AT.getKey());
		assertEquals("dataGenerators", PackageKeys.DATA_GENERATORS.getKey());
		assertEquals("description", PackageKeys.DESCRIPTION.getKey());
		assertEquals("displayName", PackageKeys.DISPLAY_NAME.getKey());
		assertEquals("email", PackageKeys.EMAIL.getKey());
		assertEquals("files", PackageKeys.FILES.getKey());
		assertEquals("fileName", PackageKeys.FILE_NAME.getKey());
		assertEquals("firstName", PackageKeys.FIRST_NAME.getKey());
		assertEquals("_id", PackageKeys.ID.getKey());
		assertEquals("lastName", PackageKeys.LAST_NAME.getKey());
		assertEquals("originalFileName", PackageKeys.ORIGINAL_FILE_NAME.getKey());
		assertEquals("packageType", PackageKeys.PACKAGE_TYPE.getKey());
		assertEquals("protocol", PackageKeys.PROTOCOL.getKey());
		assertEquals("shibId", PackageKeys.SHIBID.getKey());
		assertEquals("size", PackageKeys.SIZE.getKey());
		assertEquals("study", PackageKeys.STUDY.getKey());
        assertEquals("studyId", PackageKeys.STUDY_ID.getKey());
		assertEquals("subjectId", PackageKeys.SUBJECT_ID.getKey());
		assertEquals("submitter", PackageKeys.SUBMITTER.getKey());
		assertEquals("submitterEmail", PackageKeys.SUBMITTER_EMAIL.getKey());
		assertEquals("submitterFirstName", PackageKeys.SUBMITTER_FIRST_NAME.getKey());
		assertEquals("$oid", PackageKeys.SUBMITTER_ID.getKey());
		assertEquals("$id", PackageKeys.SUBMITTER_ID_OBJECT.getKey());
		assertEquals("submitterLastName", PackageKeys.SUBMITTER_LAST_NAME.getKey());
		assertEquals("tisInternalExperimentID", PackageKeys.TIS_INTERNAL_EXPERIMENT_ID.getKey());
		assertEquals("tisName", PackageKeys.TIS_NAME.getKey());
		assertEquals("version", PackageKeys.VERSION.getKey());
		assertEquals("largeFilesChecked", PackageKeys.LARGE_FILES_CHECKED.getKey());
	}

}
