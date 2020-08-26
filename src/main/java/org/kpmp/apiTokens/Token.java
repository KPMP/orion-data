package org.kpmp.apiTokens;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@JsonPropertyOrder({ "tokenString", "email", "expiration", "active" })
@Document(collection = "tokens")
public class Token {

    private String tokenString;
    private String shibId;
    private Date expiration;
    private Boolean active;

    public String getTokenString() {
        return tokenString;
    }

    public void setTokenString(String tokenString) {
        this.tokenString = tokenString;
    }

    public String getShibId() {
        return shibId;
    }

    public void setShibId(String shibId) {
        this.shibId = shibId;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
