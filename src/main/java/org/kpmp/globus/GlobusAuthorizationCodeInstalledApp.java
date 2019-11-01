package org.kpmp.globus;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.util.ArrayMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

public class GlobusAuthorizationCodeInstalledApp  extends AuthorizationCodeInstalledApp {
    /** Authorization code flow. */
    private final AuthorizationCodeFlow flow;

    /** Verification code receiver. */
    private final VerificationCodeReceiver receiver;

    public GlobusAuthorizationCodeInstalledApp(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver) {
        super(flow, receiver);
        this.flow = flow;
        this.receiver = receiver;
    }

    public Credential globusAuthorize(String userId) throws IOException {
        GlobusCredential globusCredential = new GlobusCredential();
        try {
            Credential credential = flow.loadCredential(userId);
            if (credential != null
                    && (credential.getRefreshToken() != null ||
                    credential.getExpiresInSeconds() == null ||
                    credential.getExpiresInSeconds() > 60)) {
                globusCredential.setCredential(credential);
                return credential;
            }
            String redirectUri = receiver.getRedirectUri();
            AuthorizationCodeRequestUrl authorizationUrl =
                    flow.newAuthorizationUrl().setRedirectUri(redirectUri);
            authorizationUrl.set("access_type", "offline");
            onAuthorization(authorizationUrl);
            String code = receiver.waitForCode();
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
            ArrayList<ArrayMap> otherTokens = (ArrayList<ArrayMap>) response.get("other_tokens");
            ArrayMap transferToken = otherTokens.get(0);
            TokenResponse transferTokenResponse = new TokenResponse();
            transferTokenResponse.setAccessToken((String)transferToken.get("access_token"));
            transferTokenResponse.setRefreshToken((String)transferToken.get("refresh_token"));
            transferTokenResponse.setTokenType((String)transferToken.get("token_type"));
            BigDecimal expires = (BigDecimal)transferToken.get("expires_in");
            transferTokenResponse.setExpiresInSeconds(expires.longValue());
            transferTokenResponse.setScope((String)transferToken.get("scope"));
            return flow.createAndStoreCredential(transferTokenResponse, userId);

        } finally {
            receiver.stop();
        }
    }
}
