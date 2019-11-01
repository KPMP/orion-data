package org.kpmp.packages;

public class PackageResponse {

    private String packageId;
    private String gdriveId;
    private String globusURL;

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getGdriveId() {
        return gdriveId;
    }

    public void setGdriveId(String gdriveId) {
        this.gdriveId = gdriveId;
    }

    public String getGlobusURL() {
        return globusURL;
    }

    public void setGlobusURL(String globusURL) {
        this.globusURL = globusURL;
    }
}
