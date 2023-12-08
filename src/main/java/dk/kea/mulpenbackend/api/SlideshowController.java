package dk.kea.mulpenbackend.api;

import dk.kea.mulpenbackend.config.ConfigProvider;
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
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/slideshow")
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
  public ResponseEntity<String> handleFileUpload(@RequestPart("file") MultipartFile file) {

    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("Please select a file to upload");
    }


    try {

      SlideshowModel slideshowModel = new SlideshowModel();
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

      slideshowModel.setFilePath(safeFileName);

      Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

      slideshowService.saveSlideshow(slideshowModel);

      return ResponseEntity.ok("File upload successful: " + safeFileName);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body("Error occurred during file upload: " + file.getOriginalFilename());
    }
  }


  @GetMapping("/all")
  public List<SlideshowModel> getAllMedia() {
    return slideshowService.getAllSlideshow();
  }

  @DeleteMapping("/deleteSlideshow/{id}")
  public ResponseEntity<String> deleteMedia(@PathVariable Long id) {
    slideshowService.deleteSlideshow(id);
    return ResponseEntity.ok("File deleted");
  }
}
