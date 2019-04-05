package org.kpmp.packages;

public enum PackageKeys {

	CLASS("_class"), CREATED_AT("createdAt"), DISPLAY_NAME("displayName"), EMAIL("email"), FILES("files"),
	FILE_NAME("fileName"), FIRST_NAME("firstName"), ID("_id"), LAST_NAME("lastName"), REGENERATE_ZIP("regenerateZip"),
	SIZE("size"), SUBMITTER("submitter"), SUBMITTER_EMAIL("submitterEmail"), SUBMITTER_FIRST_NAME("submitterFirstName"),
	SUBMITTER_ID("$oid"), SUBMITTER_ID_OBJECT("$id"), SUBMITTER_LAST_NAME("submitterLastName"), VERSION("version");

	private String key;

	private PackageKeys(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}
}
