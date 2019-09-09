package org.kpmp.packages.state;

class State {

	private String packageId;
	private String state;
	private String codicil;

	public State(String packageId, String state, String codicil) {
		this.packageId = packageId;
		this.state = state;
		this.codicil = codicil;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCodicil() {
		return codicil;
	}

	public void setCodicil(String codicil) {
		this.codicil = codicil;
	}

}
