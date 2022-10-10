package org.kpmp.dmd;

import org.kpmp.logging.LoggingService;
import org.kpmp.packages.Package;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DluPackageInventoryService {

    @Value("${data-manager.service.host}")
    private String dataManagerHost;
    @Value("${data-manager.service.endpoint}")
    private String dataManagerEndpoint;
    private RestTemplate restTemplate;
    private LoggingService logger;

    @Autowired
    public DluPackageInventoryService(RestTemplate restTemplate, LoggingService logger) {
        this.restTemplate = restTemplate;
        this.logger = logger;
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

    public String sendNewPackage(Package myPackage) {
        DluPackageInventory dluPackageInventory = this.getDluPackageInventoryFromPackage(myPackage);
        String dluPackageInventoryId = restTemplate.postForObject(dataManagerHost + dataManagerEndpoint + "/package",
                dluPackageInventory, String.class);
        if (dluPackageInventoryId == null) {
            logger.logErrorMessage(this.getClass(), null, myPackage.getPackageId(),
                    this.getClass().getSimpleName() + ".sendNewPackage",
                    "Error saving package to DMD: " + myPackage.getPackageId());
        }
        return dluPackageInventoryId;

    }
}
