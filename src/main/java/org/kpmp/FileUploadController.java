package org.kpmp;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("files") MultipartFile[] uploadfiles) {
        String responseString = "Success! You uploaded: ";
        for (MultipartFile file : uploadfiles) {
            responseString += file.getOriginalFilename() + " (" + file.getSize() + " bytes) ";
        }
        System.out.println(responseString);
        return "{\"message\": \"" + responseString + "\"}";
    }

}
