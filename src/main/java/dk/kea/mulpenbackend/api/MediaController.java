package dk.kea.mulpenbackend.api;

import dk.kea.mulpenbackend.config.ConfigProvider;
import dk.kea.mulpenbackend.Entity.MediaModel;
import dk.kea.mulpenbackend.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/media")
@CrossOrigin
@RestController
public class MediaController {
    private final ConfigProvider configProvider;

    @Autowired
    private MediaService mediaService;


    @Autowired
    public MediaController(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    /*
    @GetMapping()
    public ResponseEntity<Void> getVideos() {

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("viewVideo.html"));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

     */

    @GetMapping("/upload/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        String safeFileName = FilenameUtils.getName(filename);
        Path filePath = Paths.get(configProvider.uploadDirectory, safeFileName);
        Resource resource = new org.springframework.core.io.PathResource(filePath);

        try {
            // Set content-type dynamically based on the file type
            String contentType = Files.probeContentType(filePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    //.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    private String[] badExtensions = {"java", "htm", "html"};
    private String[] allowedExtensions = {
            "jpg", "png", "jpeg", "gif",
            "mp4", "mov", "mkv", "avi", "mp3", "wav", "flac", "webm", "webp"
    };

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestPart("file") MultipartFile file, @RequestParam("description") String description) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }


        try {

            MediaModel mediaModel = new MediaModel();
            String safeFileName = FilenameUtils.getName(file.getOriginalFilename());
            String extension = FilenameUtils.getExtension(safeFileName);
            if (extension == null) {
                return ResponseEntity.badRequest().body("File type not allowed");
            }

            extension = extension.toLowerCase();
            if (Arrays.asList(badExtensions).contains(extension)) {
                return ResponseEntity.badRequest().body("File type not allowed");
            }
            if (!Arrays.asList(allowedExtensions).contains(extension)) {
                return ResponseEntity.badRequest().body("File type not allowed");
            }

            Path uploadPath = Paths.get(configProvider.uploadDirectory, safeFileName);

            mediaModel.setFilePath(safeFileName);
            mediaModel.setDescription(description);

            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

            mediaService.saveMedia(mediaModel);

            return ResponseEntity.ok("File upload successful: " + safeFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error occurred during file upload: " + file.getOriginalFilename());
        }
    }

    @GetMapping("/all")
    public List<MediaModel> getAllMedia() {
        return mediaService.getAllMedia();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMedia(@PathVariable Long id) {
        mediaService.deleteMedia(id);
        return ResponseEntity.ok("File deleted");
    }
}

