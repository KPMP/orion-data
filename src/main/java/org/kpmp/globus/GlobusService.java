package org.kpmp.globus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

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
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.common.reflect.TypeToken;

@Service
public class GlobusService {

    private static GsonFactory JSON_FACTORY = new GsonFactory();
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

	public String getTopDirectory(String dirName) {
		String topDirectory = env.getProperty("GLOBUS_DIR");
		String fullDirName = topDirectory + "/" + dirName;
		return getFileManagerUrl(fullDirName);
	}

	protected String getFileManagerUrl(String fullDirName) {
		return fileManagerUrl + "?origin_id=" + endpointID + "&origin_path=" + fullDirName;
	}

	public List<GlobusFileListing> getFilesAndDirectoriesAtEndpoint(String packageId) throws JsonProcessingException, IOException {
		String topDirectory = env.getProperty("GLOBUS_DIR");
		String fullDirName = topDirectory + "/" + packageId;
		fullDirName.replace(" ", "+");
		GenericUrl url = new GenericUrl(API_URL + "/operation/endpoint/" + endpointID + "/ls?path=" + fullDirName + "&type:dir");
		
		HttpRequest request = requestFactory.buildGetRequest(url);
		request.setParser(new JsonObjectParser(JSON_FACTORY));

		Type type = new TypeToken<GlobusListingResponse>() {}.getType();
		HttpResponse response = request.execute();
		GlobusListingResponse globusListing = (GlobusListingResponse) response.parseAs(type);

		return globusListing.getData();
	}
}