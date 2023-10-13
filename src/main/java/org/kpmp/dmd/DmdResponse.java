package org.kpmp.dmd;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.ArrayList;
import java.util.List;

public class DmdResponse {

    private boolean success;
    @JsonAlias({ "file_list" })
    private List<DMDResponseFile> fileList;
    private String message;


    public static class DMDResponseFile {
        private String name;
        private long size;
        private String checksum;
        private String path;

        @JsonAlias({ "file_id" })
        private String fileId;
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
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

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }
    }
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

    public List<String> getFileNameList() {
        ArrayList<String> fileNames = new ArrayList<>();
        for (DMDResponseFile file : fileList) {
            fileNames.add(file.getName());
        }
        return fileNames;
    }
}
