package org.kpmp.apiTokens;

import org.springframework.lang.Nullable;

public class TokenResponse {
    private String message;
    private Token token;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
