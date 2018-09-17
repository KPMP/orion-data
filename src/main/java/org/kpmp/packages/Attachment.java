package org.kpmp.packages;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "packages")
public class Attachment {

	@Id
	@Field("universalId")
	private String id;
	private long size;
	private String fileName;

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

}
