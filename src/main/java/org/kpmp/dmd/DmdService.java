package org.kpmp.dmd;

import org.kpmp.logging.LoggingService;
import org.kpmp.packages.Attachment;
import org.kpmp.packages.Package;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class DmdService {

    @Value("${data-manager.service.host}")
    private String dataManagerHost;
    @Value("${data-manager.service.endpoint}")
    private String dataManagerEndpoint;
    private RestTemplate restTemplate;
    private LoggingService logger;

    @Autowired
    public DmdService(RestTemplate restTemplate, LoggingService logger) {
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

    public DluFile getDluFileFromAttachment(Attachment attachment, String packageId) {
        DluFile file = new DluFile();
        file.setDluFileName(attachment.getFileName());
        file.setDluFileId(attachment.getId());
        file.setDluPackageId(packageId);
        file.setDluMd5Checksum(attachment.getMd5checksum());
        file.setDluFileSize(attachment.getSize());
        return file;
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

    public List sendPackageFiles(Package myPackage) {
        List fileIds = new ArrayList<>();
        for (Attachment file : myPackage.getAttachments()) {
            fileIds.add(sendNewFile(file, myPackage.getPackageId()));
        }
        return fileIds;
    }

    public String sendNewFile(Attachment attachment, String packageId) {
        DluFile file = getDluFileFromAttachment(attachment, packageId);
        String dluFileId = restTemplate.postForObject(dataManagerHost + dataManagerEndpoint + "/file",
                file, String.class);
        if (dluFileId == null) {
            logger.logErrorMessage(this.getClass(), null, file.getDluFileId(),
                    this.getClass().getSimpleName() + ".sendNewPackage",
                    "Error saving file to DMD. Package: " + file.getDluPackageId() + ", File: " +file.getDluFileId());
        }
        return dluFileId;
    }

    public String setPackageInError(String packageId) {
        HashMap payload = new HashMap<>();
        payload.put("dlu_error", true);
        String retPackageId = restTemplate.postForObject(dataManagerHost + dataManagerEndpoint + "/package/" + packageId,
                payload, String.class);
        return retPackageId;
    }
}
