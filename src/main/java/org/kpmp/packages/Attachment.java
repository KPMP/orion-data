package org.kpmp.packages;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "fileName", "size", "id" })
@Document(collection = "packages")
public class Attachment {

	@Id
	@Field("universalId")
	private String id;
	private long size;
	private String fileName;
	private String md5checksum;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String filename) {
		this.fileName = filename;
	}

	public String getMd5checksum() {
		return md5checksum;
	}

	public void setMd5checksum(String md5checksum) {
		this.md5checksum = md5checksum;
	}
}
