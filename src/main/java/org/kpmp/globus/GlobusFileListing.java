package org.kpmp.globus;

import com.google.api.client.util.Key;

public class GlobusFileListing {

	@Key("DATA_TYPE")
	private String dataType;
	@Key
	private String name;
	@Key
	private String type;
	@Key
	private long size;

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
