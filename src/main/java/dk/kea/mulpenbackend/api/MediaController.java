package dk.kea.mulpenbackend.api;

import dk.kea.mulpenbackend.config.ConfigProvider;
import dk.kea.mulpenbackend.model.MediaModel;
import dk.kea.mulpenbackend.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

        Path uploadPath = Paths.get(configProvider.uploadDirectory, safeFileName);
        Path thumbnailPath = Paths.get(configProvider.thumbnailDirectory, safeFileName);

        Path filePath = Files.exists(uploadPath) ? uploadPath : thumbnailPath;

        if (!Files.exists(filePath)) {
            // Handle the case where the file is not found in both directories
            return ResponseEntity.notFound().build();
        }

        Resource resource = new org.springframework.core.io.PathResource(filePath);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(Files.probeContentType(filePath)));

            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String[] badExtensions = {"java", "htm", "html"};
    private String[] allowedFileExtensions = {
            "jpg", "png", "jpeg", "gif",
            "mp4", "mov", "mkv", "avi", "mp3", "wav", "flac", "webm", "webp"
    };

    private String [] allowedThumbnailExtensions = {
            "jpg", "png", "jpeg"
    };



    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestPart("file") MultipartFile file, @RequestPart("thumbnail") MultipartFile thumbnail, @RequestParam("description") String description) {

        System.out.println("Upload controller executed");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccessControlAllowOrigin("*");

        //Handle file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        //Handle thumbnail
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a thumbnail to upload");
        }


        try {
            //Setup file
            MediaModel fileMediaModel = new MediaModel();
            String fileSafeFileName = FilenameUtils.getName(file.getOriginalFilename());
            String fileExtension = FilenameUtils.getExtension(fileSafeFileName);

            //Setup thumbnail
            MediaModel thumbnailMediaModel = new MediaModel();
            String thumbnailSafeFileName = FilenameUtils.getName(thumbnail.getOriginalFilename());
            String thumbnailExtension = FilenameUtils.getExtension(thumbnailSafeFileName);

            //If file extensions are null
            if (fileExtension == null) {
                return ResponseEntity.badRequest().body("File type not allowed");
            }

            if (thumbnailExtension == null) {
                return ResponseEntity.badRequest().body("File type not allowed");
            }

            //File extensions to lowercase for consistency
            fileExtension = fileExtension.toLowerCase();
            thumbnailExtension = thumbnailExtension.toLowerCase();


            //Check file extension
            if (Arrays.asList(badExtensions).contains(fileExtension) || Arrays.asList(badExtensions).contains(thumbnailExtension)) {
                return ResponseEntity.badRequest().body("File type not allowed");
            }


            if (!Arrays.asList(allowedFileExtensions).contains(fileExtension)) {
                return ResponseEntity.badRequest().body("File type not allowed");
            }

            if (!Arrays.asList(allowedThumbnailExtensions).contains(thumbnailExtension)){
                return ResponseEntity.badRequest().body("Thumbnail file type not allowed");
            }

            Path fileUploadPath = Paths.get(configProvider.uploadDirectory, fileSafeFileName);
            Path thumbnailUploadPath = Paths.get(configProvider.thumbnailDirectory, thumbnailSafeFileName);


            //Set attributes
            fileMediaModel.setFilePath(fileSafeFileName);
            fileMediaModel.setDescription(description);
            fileMediaModel.setThumbnailFilePath(thumbnailSafeFileName);

            thumbnailMediaModel.setFilePath(thumbnailSafeFileName);
            thumbnailMediaModel.setDescription(description);

            Files.copy(file.getInputStream(), fileUploadPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(thumbnail.getInputStream(), thumbnailUploadPath, StandardCopyOption.REPLACE_EXISTING);

            //Safe media
            mediaService.saveMedia(fileMediaModel);
            mediaService.saveMedia(thumbnailMediaModel);

            return new ResponseEntity<>("File upload successful: " + fileSafeFileName + ", " + thumbnailSafeFileName, headers, HttpStatus.OK);
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

