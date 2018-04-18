package org.kpmp;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {

    @RequestMapping(value = "/upload_old", method = RequestMethod.POST)
    public void uploadFile(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        System.out.println(payload);
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("files") MultipartFile[] uploadfiles) {
        String responseString = "You uploaded:";
        for (MultipartFile file : uploadfiles) {
            System.out.println(file.getOriginalFilename() + ": " + file.getSize());
            responseString += file.getOriginalFilename() + ": " + file.getSize() + "<br/>";
        }
        return responseString;
    }

}
