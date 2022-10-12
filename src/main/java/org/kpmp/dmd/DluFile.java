package org.kpmp.dmd;

public class DluFile {

    private String dluFileName;
    private String dluPackageId;
    private String dluFileId;
    private long dluFileSize;
    private String dluMd5Checksum;

    public String getDluFileName() {
        return dluFileName;
    }

    public void setDluFileName(String dluFileName) {
        this.dluFileName = dluFileName;
    }

    public long getDluFileSize() {
        return dluFileSize;
    }

    public void setDluFileSize(long dluFileSize) {
        this.dluFileSize = dluFileSize;
    }

    public String getDluMd5Checksum() {
        return dluMd5Checksum;
    }

    public void setDluMd5Checksum(String dluMd5Checksum) {
        this.dluMd5Checksum = dluMd5Checksum;
    }

    public String getDluPackageId() {
        return dluPackageId;
    }

    public void setDluPackageId(String dluPackageId) {
        this.dluPackageId = dluPackageId;
    }

    public String getDluFileId() {
        return dluFileId;
    }

    public void setDluFileId(String dluFileId) {
        this.dluFileId = dluFileId;
    }
}
