package org.kpmp.globus;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;

@Service
public class GlobusAuthService {

    private final File DATA_STORE_DIR =
            new File("globus_tokens");

    private final String apiKey;
    private final String apiSecret;
    private static GsonFactory JSON_FACTORY;

    public GlobusAuthService(Environment env) {
        this.apiKey = env.getProperty("GLOBUS_API_KEY");
        this.apiSecret = env.getProperty("GLOBUS_API_SECRET");
    }

    public Credential authorize(HttpTransport httpTransport) throws Exception {
        String tokenServerUrl = "https://auth.globus.org/v2/oauth2/token";
        String authServerUrl = "https://auth.globus.org/v2/oauth2/authorize";
        AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(BearerToken
                .authorizationHeaderAccessMethod(),
                httpTransport,
                JSON_FACTORY,
                new GenericUrl(tokenServerUrl),
                new ClientParametersAuthentication(
                        apiKey, apiSecret),
                apiKey,
                authServerUrl).setScopes(Arrays.asList("openid", "profile", "email", "urn:globus:auth:scope:transfer.api.globus.org:all"))
                .setDataStoreFactory(new FileDataStoreFactory(DATA_STORE_DIR)).build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setHost(
                "localhost").setPort(8888).build();
        return new GlobusAuthorizationCodeInstalledApp(flow, receiver).globusAuthorize("user");
    }

}
