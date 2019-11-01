package org.kpmp.globus;

import com.google.api.client.auth.oauth2.Credential;

public class GlobusCredential  {
    private Credential credential;
    private String transferCredential;

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public String getTransferCredential() {
        return transferCredential;
    }

    public void setTransferCredential(String transferCredential) {
        this.transferCredential = transferCredential;
    }


}
