package org.kpmp.packages;

import java.util.Date;

public class State {

	private String packageId;
	private String state;
	private String codicil;
	private Boolean largeUploadChecked;
	private Date stateChangeDate;

	public State() {
	}

	public State(String packageId, String state, Boolean largeUploadChecked, String codicil) {
		this.packageId = packageId;
		this.state = state;
		this.largeUploadChecked = largeUploadChecked;
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

	public Boolean getLargeUploadChecked() { return largeUploadChecked; }

	public void setLargeUploadChecked(Boolean largeUploadChecked) { this.largeUploadChecked = largeUploadChecked; }

	public Date getStateChangeDate() {
		return stateChangeDate;
	}

	public void setStateChangeDate(Date stateChangedDate) {
		this.stateChangeDate = stateChangedDate;
	}

}
