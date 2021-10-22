package org.kpmp.packages;

public enum AttachmentKeys {

	CLASS("_class"), ID("id"), SIZE("size"), FILENAME("fileName"),
	MD5CHECkSUM("md5checksum");

	private String key;

	private AttachmentKeys(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}
}
