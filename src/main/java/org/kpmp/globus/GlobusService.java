package org.kpmp.globus;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.*;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import org.apache.commons.io.IOUtils;
import org.kpmp.logging.LoggingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
    public class GlobusService  {
    private LoggingService logger;

    private final File DATA_STORE_DIR =
            new File("globus_tokens");

    private  FileDataStoreFactory DATA_STORE_FACTORY;
    private  JsonFactory JSON_FACTORY = new JacksonFactory();
    private HttpRequestFactory requestFactory;

    private final String TOKEN_SERVER_URL = "https://auth.globus.org/v2/oauth2/token";
    private final String AUTHORIZATION_SERVER_URL =
            "https://auth.globus.org/v2/oauth2/authorize";
    private  final String API_URL = "https://transfer.api.globusonline.org/v0.10";

    @Value("${globus.endpoint.ID}")
    private String endpointID;

    @Value("${globus.top.directory}")
    private String topDirectory;

    @Value("${globus.file.manager.url}")
    private String fileManagerUrl;


        public GlobusService(Environment env, LoggingService logger) throws Exception {
            this.logger = logger;
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Credential credential = authorize(httpTransport);
            requestFactory = httpTransport.createRequestFactory(credential);
        }

        private Credential authorize(HttpTransport httpTransport) throws Exception {
            AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken
                    .authorizationHeaderAccessMethod(),
                    httpTransport,
                    JSON_FACTORY,
                    new GenericUrl(TOKEN_SERVER_URL),
                    new ClientParametersAuthentication(
                            GlobusClientCredentials.API_KEY, GlobusClientCredentials.API_SECRET),
                    GlobusClientCredentials.API_KEY,
                    AUTHORIZATION_SERVER_URL).setScopes(Arrays.asList("openid", "profile", "email", "urn:globus:auth:scope:transfer.api.globus.org:all"))
                    .setDataStoreFactory(DATA_STORE_FACTORY).build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost(
                    GlobusClientCredentials.DOMAIN).setPort(GlobusClientCredentials.PORT).build();

            return new GlobusAuthorizationCodeInstalledApp(flow, receiver).globusAuthorize("user");
        }

        public String getEndpointInfo() throws IOException {
            GenericUrl url = new GenericUrl(API_URL + "/endpoint/b6df3b6a-f9bd-11e9-8a5d-0e35e66293c2/activation_requirements");
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse response = request.execute();
            return IOUtils.toString(response.getContent(), StandardCharsets.UTF_8);
        }

        public String createDirectory(String dirName) throws IOException {
            GenericUrl url = new GenericUrl(API_URL + "/operation/endpoint/" + endpointID + "/mkdir");
            String fullDirName = topDirectory + dirName;
            String body = "{\n" +
                    "  \"path\": \"" + fullDirName + "\"," +
                    "  \"DATA_TYPE\": \"mkdir\"" +
                    "}";
            HttpRequest request = requestFactory.buildPostRequest(url, ByteArrayContent.fromString("application/json", body));
            request.execute();
            return getFileManagerUrl(fullDirName);
        }

        public String getFileManagerUrl(String fullDirName) {
            return fileManagerUrl + "?origin_id=" + endpointID + "&origin_path=" + fullDirName;
        }
    }
