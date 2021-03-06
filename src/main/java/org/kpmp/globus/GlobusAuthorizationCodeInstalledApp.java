package org.kpmp.globus;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.util.ArrayMap;

public class GlobusAuthorizationCodeInstalledApp extends AuthorizationCodeInstalledApp {
	private final AuthorizationCodeFlow flow;
	private final VerificationCodeReceiver receiver;

	public GlobusAuthorizationCodeInstalledApp(AuthorizationCodeFlow flow, VerificationCodeReceiver receiver) {
		super(flow, receiver);
		this.flow = flow;
		this.receiver = receiver;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Credential globusAuthorize(String userId) throws IOException {
		try {
			Credential credential = flow.loadCredential(userId);
			if (credential != null && (credential.getRefreshToken() != null || credential.getExpiresInSeconds() == null
					|| credential.getExpiresInSeconds() > 60)) {
				return credential;
			}
			String redirectUri = receiver.getRedirectUri();
			AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
			authorizationUrl.set("access_type", "offline");
			onAuthorization(authorizationUrl);
			String code = receiver.waitForCode();
			TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
			ArrayList<ArrayMap> otherTokens = (ArrayList<ArrayMap>) response.get("other_tokens");
			ArrayMap transferToken = otherTokens.get(0);
			TokenResponse transferTokenResponse = new TokenResponse();
			transferTokenResponse.setAccessToken((String) transferToken.get("access_token"));
			transferTokenResponse.setRefreshToken((String) transferToken.get("refresh_token"));
			transferTokenResponse.setTokenType((String) transferToken.get("token_type"));
			BigDecimal expires = (BigDecimal) transferToken.get("expires_in");
			transferTokenResponse.setExpiresInSeconds(expires.longValue());
			transferTokenResponse.setScope((String) transferToken.get("scope"));
			return flow.createAndStoreCredential(transferTokenResponse, userId);

		} finally {
			receiver.stop();
		}
	}
}
