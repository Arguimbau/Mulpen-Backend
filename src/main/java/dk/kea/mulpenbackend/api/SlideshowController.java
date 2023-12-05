package dk.kea.mulpenbackend.api;

import dk.kea.mulpenbackend.config.ConfigProvider;
import dk.kea.mulpenbackend.model.MediaModel;
import dk.kea.mulpenbackend.service.MediaService;
import dk.kea.mulpenbackend.service.SlideshowService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/slideshow")
@CrossOrigin
public class SlideshowController {

    private final ConfigProvider configProvider;

    @Autowired
    private SlideshowService slideshowService;

    @Autowired
    public SlideshowController(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    private final String[] badExtensions = {"java", "htm", "html"};
    private final String[] allowedExtensions = {
            "jpg", "png", "jpeg"
    };

    @PostMapping("/uploadSlideshow")
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

            Path uploadPath = Paths.get(configProvider.slideshowDirectory, safeFileName);

            mediaModel.setFilePath(safeFileName);
            mediaModel.setDescription(description);

            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

            slideshowService.saveSlideshow(mediaModel);

            return ResponseEntity.ok("File upload successful: " + safeFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error occurred during file upload: " + file.getOriginalFilename());
        }
    }

    @GetMapping("/all")
    public List<MediaModel> getAllMedia() {
        return slideshowService.getAllSlideshow();
    }
}
