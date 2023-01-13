package org.kpmp.dmd;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public class DMDResponse {
    private boolean success;
    @JsonAlias({ "file_list" })
    private List fileList;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List getFileList() {
        return fileList;
    }

    public void setFileList(List fileList) {
        this.fileList = fileList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
