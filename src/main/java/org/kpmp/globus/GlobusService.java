package org.kpmp.globus;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;

@Service
public class GlobusService {

	private HttpRequestFactory requestFactory;

	@Value("${globus.endpoint.ID}")
	private String endpointID;

	@Value("${globus.top.directory}")
	private String topDirectory;

	@Value("${globus.file.manager.url}")
	private String fileManagerUrl;

	private class GlobusTransferRequest {
		private String path;
		private String dataType;

		@SuppressWarnings("unused")
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

	public GlobusService(HttpTransport httpTransport, GlobusAuthService globusAuthService) throws Exception {
		Credential credential = globusAuthService.authorize(httpTransport);
		requestFactory = httpTransport.createRequestFactory(credential);
	}

	public String createDirectory(String dirName) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		String apiUrl = "https://transfer.api.globusonline.org/v0.10";
		GenericUrl url = new GenericUrl(apiUrl + "/operation/endpoint/" + endpointID + "/mkdir");
		String fullDirName = topDirectory + dirName;
		GlobusTransferRequest globusTransferRequest = new GlobusTransferRequest();
		globusTransferRequest.setPath(fullDirName);
		globusTransferRequest.setDataType("mkdir");
		HttpRequest request = requestFactory.buildPostRequest(url,
				ByteArrayContent.fromString("application/json", mapper.writeValueAsString(globusTransferRequest)));
		request.execute();
		return getFileManagerUrl(fullDirName);
	}

	public String getFileManagerUrl(String fullDirName) {
		return fileManagerUrl + "?origin_id=" + endpointID + "&origin_path=" + fullDirName;
	}
}
