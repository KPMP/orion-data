package org.kpmp.dmd;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.ArrayList;
import java.util.List;

public class DMDResponse {

    public static class DMDResponseFile {
        private String name;
        private int size;
        private String checksum;
        private String path;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getChecksum() {
            return checksum;
        }

        public void setChecksum(String checksum) {
            this.checksum = checksum;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
    private boolean success;
    @JsonAlias({ "file_list" })
    private List<DMDResponseFile> fileList;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DMDResponseFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<DMDResponseFile> fileList) {
        this.fileList = fileList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List getFileNameList() {
        ArrayList fileNames = new ArrayList();
        for (DMDResponseFile file : fileList) {
            fileNames.add(file.getName());
        }
        return fileNames;
    }
}
