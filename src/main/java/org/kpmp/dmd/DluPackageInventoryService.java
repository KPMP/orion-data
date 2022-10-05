package org.kpmp.dmd;

import org.kpmp.packages.Package;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DluPackageInventoryService {

    private DluPackageInventoryRepository dluPackageInventoryRepository;

    @Autowired
    public DluPackageInventoryService(DluPackageInventoryRepository dluPackageInventoryRepository) {
        this.dluPackageInventoryRepository = dluPackageInventoryRepository;
    }

    public DluPackageInventory getDluPackageInventoryFromPackage(Package myPackage) {
        DluPackageInventory dluPackageInventory = new DluPackageInventory();
        dluPackageInventory.setDluPackageId(myPackage.getPackageId());
        dluPackageInventory.setDluCreated(myPackage.getCreatedAt());
        dluPackageInventory.setDluSubmitter(myPackage.getSubmitter().getDisplayName());
        dluPackageInventory.setDluTis(myPackage.getTisName());
        dluPackageInventory.setDluPackageType(myPackage.getPackageType());
        dluPackageInventory.setDluSubjectId(myPackage.getSubjectId());
        dluPackageInventory.setDluError(false);
        dluPackageInventory.setDluLfu(myPackage.getLargeFilesChecked());
        return dluPackageInventory;
    }

    public void saveFromPackage(Package myPackage) {
        DluPackageInventory dluPackageInventory = this.getDluPackageInventoryFromPackage(myPackage);
        dluPackageInventoryRepository.save(dluPackageInventory);
    }
}
