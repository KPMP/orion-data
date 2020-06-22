package org.kpmp.globus;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;

@Service
public class GlobusService {

	private static final String API_URL = "https://transfer.api.globusonline.org/v0.10";

	private HttpRequestFactory requestFactory;

	@Value("${globus.endpoint.ID}")
	private String endpointID;

	@Value("${globus.file.manager.url}")
	private String fileManagerUrl;

	private Environment env;

	public GlobusService(HttpTransport httpTransport, GlobusAuthService globusAuthService, Environment env)
			throws Exception {
		Credential credential = globusAuthService.authorize(httpTransport);
		requestFactory = httpTransport.createRequestFactory(credential);
		this.env = env;
	}

	public String createDirectory(String dirName) throws IOException {
		String topDirectory = env.getProperty("GLOBUS_DIR");
		ObjectMapper mapper = new ObjectMapper();
		GenericUrl url = new GenericUrl(API_URL + "/operation/endpoint/" + endpointID + "/mkdir");
		String fullDirName = topDirectory + "/" + dirName;
		GlobusTransferRequest globusTransferRequest = new GlobusTransferRequest();
		globusTransferRequest.setPath(fullDirName);
		globusTransferRequest.setDataType("mkdir");
		HttpRequest request = requestFactory.buildPostRequest(url,
				ByteArrayContent.fromString("application/json", mapper.writeValueAsString(globusTransferRequest)));
		request.execute();
		return getFileManagerUrl(fullDirName);
	}

	protected String getFileManagerUrl(String fullDirName) {
		return fileManagerUrl + "?origin_id=" + endpointID + "&origin_path=" + fullDirName;
	}

	public String getFilesAtEndpoint(String packageId) throws JsonProcessingException, IOException {
		String topDirectory = env.getProperty("GLOBUS_DIR");
		String fullDirName = topDirectory + "/" + packageId;

		GenericUrl url = new GenericUrl(API_URL + "/operation/endpoint/" + endpointID + "/ls?path=" + fullDirName);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse response = request.execute();

		return response.parseAsString();
	}
}