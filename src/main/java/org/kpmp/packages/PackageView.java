package org.kpmp.packages;

import java.io.IOException;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class PackageView {

	private boolean isDownloadable;
	private JsonNode packageInfo;
	private ObjectMapper mapper;

	public PackageView(JSONObject packageJSON) throws IOException {
		mapper = new ObjectMapper();
		this.packageInfo = mapper.readTree(packageJSON.toString());
	}

	public void setIsDownloadable(boolean isDownloadable) {
		this.isDownloadable = isDownloadable;
	}

	public boolean isDownloadable() {
		return isDownloadable;
	}

	public JsonNode getPackageInfo() {
		return packageInfo;
	}

	public void setPackageInfo(JSONObject packageJSON) throws IOException {
		this.packageInfo = mapper.readTree(packageJSON.toString());
	}
}
