package org.kpmp.packages;

import java.io.IOException;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class PackageView {

	private JsonNode packageInfo;
	private ObjectMapper mapper;
	private State state;
	private String globusMoveStatus;
	private String errorMessage;

	public PackageView(JSONObject packageJSON) throws IOException {
		mapper = new ObjectMapper();
		this.packageInfo = mapper.readTree(packageJSON.toString());
	}

	public JsonNode getPackageInfo() {
		return packageInfo;
	}

	public void setPackageInfo(JSONObject packageJSON) throws IOException {
		this.packageInfo = mapper.readTree(packageJSON.toString());
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getGlobusMoveStatus() {
		return globusMoveStatus;
	}

	public void setGlobusMoveStatus(String state) {
		if (state.contains("error")) {
			String[] bits = state.split(":");
			if (bits.length > 1) {
				errorMessage = bits[1];
				globusMoveStatus = bits[0];
			}
			else {
				errorMessage = bits[0];
				globusMoveStatus = "error";
			}
		} else {
			globusMoveStatus = state;
		}
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
