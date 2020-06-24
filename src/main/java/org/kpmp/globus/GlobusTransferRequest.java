package org.kpmp.globus;

import com.fasterxml.jackson.annotation.JsonProperty;

class GlobusTransferRequest {
	private String path;
	private String dataType;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@JsonProperty("DATA_TYPE")
	public String getDataType() {
		return dataType;
	}

	@JsonProperty("DATA_TYPE")
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
