package org.kpmp.dmd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public String convertAndSendNewPackage(Package myPackage) {
        DluPackageInventory dluPackageInventory = new DluPackageInventory(myPackage);
        sendNewPackage(dluPackageInventory);
        return dluPackageInventory.getDluPackageId();
    }


    public String sendNewPackage(DluPackageInventory dluPackageInventory) {
        String dluPackageInventoryId = restTemplate.postForObject(dataManagerHost + dataManagerEndpoint + "/package",
                dluPackageInventory, String.class);
        if (dluPackageInventoryId == null) {
            logger.logErrorMessage(this.getClass(), null, dluPackageInventory.getDluPackageId(),
                    this.getClass().getSimpleName() + ".sendNewPackage",
                    "Error saving package to DMD: " + dluPackageInventory.getDluPackageId());
        }
        return dluPackageInventoryId;
    }

    public List sendPackageFiles(Package myPackage) {
        List fileIds = new ArrayList<>();
        for (Attachment attachment : myPackage.getAttachments()) {
            DluFile file = new DluFile(attachment, myPackage.getPackageId());
            fileIds.add(file.getDluFileId());
            sendNewFile(file);
        }
        return fileIds;
    }

    public String sendNewFile(DluFile file) {
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

    public DMDResponse moveFiles(String packageId) throws JsonProcessingException {
        HashMap payload = new HashMap<>();
        String response = restTemplate.postForObject(dataManagerHost + dataManagerEndpoint + "/package/" + packageId + "/move",
                payload, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        DMDResponse dmdResponse = objectMapper.readValue(response, DMDResponse.class);
        return dmdResponse;
    }


}
