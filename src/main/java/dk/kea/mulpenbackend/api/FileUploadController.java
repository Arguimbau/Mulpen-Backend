package dk.kea.mulpenbackend.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
/*
@RestController
@CrossOrigin
public class FileUploadController {
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestPart("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            String uploadDirectory = System.getenv("MEDIA_FILE_PATH");

            Path uploadPath = Paths.get(uploadDirectory, file.getOriginalFilename());

            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("File upload successful:" + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error occurred during file upload");
        }
    }
}

 */
