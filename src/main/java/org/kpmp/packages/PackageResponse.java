package org.kpmp.packages;

public class PackageResponse {

    private String packageId;
    private String globusURL;

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getGlobusURL() {
        return globusURL;
    }

    public void setGlobusURL(String globusURL) {
        this.globusURL = globusURL;
    }
}
