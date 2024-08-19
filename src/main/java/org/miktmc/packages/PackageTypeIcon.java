package org.miktmc.packages;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "packageTypeIcons")
public class PackageTypeIcon {

    String iconType;
    List<String> packageTypes;

    public String getIconType() {
        return iconType;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
    }

    public List<String> getPackageTypes() {
        return packageTypes;
    }

    public void setPackageTypes(List<String> packageType) {
        this.packageTypes = packageType;
    }
}