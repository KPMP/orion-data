package org.kpmp.dmd;
import org.kpmp.packages.Package;

import java.util.Date;

public class DluPackageInventory {

    private String dluPackageId;
    private Date dluCreated;
    private String dluSubmitter;
    private String dluTis;
    private String dluPackageType;
    private String dluSubjectId;
    private Boolean dluError;
    private Boolean dluLfu;
    private String knownSpecimen;
    private String redcapId;
    private Boolean userPackageReady;
    private Boolean dvcValidationComplete;
    private String packageValidated;
    private String readyToMoveFromGlobus;
    private Boolean globusDluStatus;
    private String removedFromGlobus;
    private String promotionStatus;
    private String notes;

    public DluPackageInventory(Package myPackage) {
        setDluPackageId(myPackage.getPackageId());
        setDluCreated(myPackage.getCreatedAt());
        setDluSubmitter(myPackage.getSubmitter().getDisplayName());
        setDluTis(myPackage.getTisName());
        setDluPackageType(myPackage.getPackageType());
        setDluSubjectId(myPackage.getSubjectId());
        setDluError(false);
        setDluLfu(myPackage.getLargeFilesChecked());
        setGlobusDluStatus(false);
    }

    public DluPackageInventory() {
    }

    public String getDluPackageId() {
        return dluPackageId;
    }

    public void setDluPackageId(String dluPackageId) {
        this.dluPackageId = dluPackageId;
    }

    public Date getDluCreated() {
        return dluCreated;
    }

    public void setDluCreated(Date dluCreated) {
        this.dluCreated = dluCreated;
    }

    public String getDluSubmitter() {
        return dluSubmitter;
    }

    public void setDluSubmitter(String dluSubmitter) {
        this.dluSubmitter = dluSubmitter;
    }

    public String getDluTis() {
        return dluTis;
    }

    public void setDluTis(String dluTis) {
        this.dluTis = dluTis;
    }

    public String getDluPackageType() {
        return dluPackageType;
    }

    public void setDluPackageType(String dluPackageType) {
        this.dluPackageType = dluPackageType;
    }

    public String getDluSubjectId() {
        return dluSubjectId;
    }

    public void setDluSubjectId(String dluSubjectId) {
        this.dluSubjectId = dluSubjectId;
    }

    public Boolean getDluError() {
        return dluError;
    }

    public void setDluError(Boolean dluError) {
        this.dluError = dluError;
    }

    public Boolean getDluLfu() {
        return dluLfu;
    }

    public void setDluLfu(Boolean dluLfu) {
        this.dluLfu = dluLfu;
    }

    public String getKnownSpecimen() {
        return knownSpecimen;
    }

    public void setKnownSpecimen(String knownSpecimen) {
        this.knownSpecimen = knownSpecimen;
    }

    public String getRedcapId() {
        return redcapId;
    }

    public void setRedcapId(String redcapId) {
        this.redcapId = redcapId;
    }

    public Boolean getUserPackageReady() {
        return userPackageReady;
    }

    public void setUserPackageReady(Boolean userPackageReady) {
        this.userPackageReady = userPackageReady;
    }

    public Boolean getDvcValidationComplete() {
        return dvcValidationComplete;
    }

    public void setDvcValidationComplete(Boolean dvcValidationComplete) {
        this.dvcValidationComplete = dvcValidationComplete;
    }

    public String getPackageValidated() {
        return packageValidated;
    }

    public void setPackageValidated(String packageValidated) {
        this.packageValidated = packageValidated;
    }

    public String getReadyToMoveFromGlobus() {
        return readyToMoveFromGlobus;
    }

    public void setReadyToMoveFromGlobus(String readyToMoveFromGlobus) {
        this.readyToMoveFromGlobus = readyToMoveFromGlobus;
    }

    public Boolean getGlobusDluStatus() {
        return globusDluStatus;
    }

    public void setGlobusDluStatus(Boolean globusDluStatus) {
        this.globusDluStatus = globusDluStatus;
    }

    public String getRemovedFromGlobus() {
        return removedFromGlobus;
    }

    public void setRemovedFromGlobus(String removedFromGlobus) {
        this.removedFromGlobus = removedFromGlobus;
    }

    public String getPromotionStatus() {
        return promotionStatus;
    }

    public void setPromotionStatus(String promotionStatus) {
        this.promotionStatus = promotionStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


}
