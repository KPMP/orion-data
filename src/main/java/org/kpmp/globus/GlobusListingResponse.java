package org.kpmp.globus;

import java.util.List;

import com.google.api.client.util.Key;

public class GlobusListingResponse {

	@Key("DATA")
	private List<GlobusFileListing> data;

	public List<GlobusFileListing> getData() {
		return data;
	}

	public void setData(List<GlobusFileListing> data) {
		this.data = data;
	}

}
