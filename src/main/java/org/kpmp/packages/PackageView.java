package org.kpmp.packages;

class PackageView {

	private boolean isDownloadable;
	private Package packageInfo;

	public PackageView(Package packageInfo) {
		this.packageInfo = packageInfo;
	}

	public void setIsDownloadable(boolean isDownloadable) {
		this.isDownloadable = isDownloadable;
	}

	public boolean isDownloadable() {
		return isDownloadable;
	}

	public Package getPackageInfo() {
		return packageInfo;
	}

	public void setPackageInfo(Package packageInfo) {
		this.packageInfo = packageInfo;
	}
}
