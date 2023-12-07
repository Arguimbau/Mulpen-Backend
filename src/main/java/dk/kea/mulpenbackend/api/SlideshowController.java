package dk.kea.mulpenbackend.api;

import dk.kea.mulpenbackend.config.ConfigProvider;
import dk.kea.mulpenbackend.model.MediaModel;
import dk.kea.mulpenbackend.model.SlideshowModel;
import dk.kea.mulpenbackend.service.SlideshowService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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


  @GetMapping("/uploadSlideshow/{filename:.+}")
  public ResponseEntity<Resource> getFile(@PathVariable String filename) {
    String safeFileName = FilenameUtils.getName(filename);
    Path filePath = Paths.get(configProvider.slideshowDirectory, safeFileName);
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

  @PostMapping("/uploadSlideshow")
  public ResponseEntity<String> handleFileUpload(@RequestPart("files") List<MultipartFile> files) {
    if (files.isEmpty()) {
      return ResponseEntity.badRequest().body("Please select at least one file to upload");
    }

    List<String> successfullyUploadedFiles = new ArrayList<>();

    for (MultipartFile file : files) {
      if (file.isEmpty()) {
        // You may want to handle this case differently, depending on your requirements.
        continue;
      }

      try {
        SlideshowModel slideshowModel = new SlideshowModel();
        String safeFileName = FilenameUtils.getName(file.getOriginalFilename());
        String extension = FilenameUtils.getExtension(safeFileName);

        if (extension == null || Arrays.asList(badExtensions).contains(extension) || !Arrays.asList(allowedExtensions).contains(extension)) {
          // Handle invalid file types
          continue;
        }

        Path uploadPath = Paths.get(configProvider.slideshowDirectory, safeFileName);
        slideshowModel.setFilePath(safeFileName);
        Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
        slideshowService.saveSlideshow(slideshowModel);

        successfullyUploadedFiles.add(safeFileName);
      } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error occurred during file upload: " + file.getOriginalFilename());
      }
    }

    if (!successfullyUploadedFiles.isEmpty()) {
      return ResponseEntity.ok("File upload successful: " + String.join(", ", successfullyUploadedFiles));
    } else {
      return ResponseEntity.badRequest().body("No valid files were uploaded");
    }
  }


  @GetMapping("/all")
  public List<SlideshowModel> getAllMedia() {
    return slideshowService.getAllSlideshow();
  }
}
