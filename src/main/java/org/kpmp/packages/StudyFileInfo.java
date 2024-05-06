package org.kpmp.packages;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "studyFileInfo")
public class StudyFileInfo {

    @Id
    private String study;
    private int fileCounter;
    private String uploadSourceLetter;
    private boolean shouldRename;

    public boolean getShouldRename() {
        return this.shouldRename;
    }

    public void setShouldRename(boolean shouldRename) {
        this.shouldRename = shouldRename;
    };

    public String getStudy() {
        return this.study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public int getFileCounter() {
        return this.fileCounter;
    }

    public void setFileCounter(int fileCounter) {
        this.fileCounter = fileCounter;
    }

    public String getUploadSourceLetter() {
        return this.uploadSourceLetter;
    }

    public void setUploadSourceLetter(String uploadSourceLetter) {
        this.uploadSourceLetter = uploadSourceLetter;
    }

}
