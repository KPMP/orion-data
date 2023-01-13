package org.kpmp.dmd;

import java.util.List;

public class DMDResponse {
    private boolean success;
    private List fileList;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List getFiles() {
        return fileList;
    }

    public void setFiles(List fileList) {
        this.fileList = fileList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
