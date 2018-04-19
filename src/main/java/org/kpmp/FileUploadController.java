package org.kpmp;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {

    @PostMapping("/multiupload")
    public String handleMultiFileUpload(@RequestParam("files") MultipartFile[] uploadfiles) {
        String responseString = "Success! You uploaded: ";
        for (MultipartFile file : uploadfiles) {
            responseString += file.getOriginalFilename() + " (" + file.getSize() + " bytes) ";
        }
        System.out.println(responseString);
        return "{\"message\": \"" + responseString + "\"}";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("qqfile") MultipartFile uploadFile) {
        String responseString = "Success! You uploaded: " +
                uploadFile.getOriginalFilename() + " (" + uploadFile.getSize() + " bytes) ";
        System.out.println(responseString);
        return "{\"message\": \"" + responseString + "\"}";
    }

}
