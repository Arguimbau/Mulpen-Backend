package dk.kea.mulpenbackend.service;

import dk.kea.mulpenbackend.config.ConfigProvider;
import dk.kea.mulpenbackend.model.SlideshowModel;
import dk.kea.mulpenbackend.repository.SlideshowRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class SlideshowService {

  SlideshowRepository slideshowRepository;

  private ConfigProvider configProvider;

  @Autowired
  public SlideshowService(SlideshowRepository slideshowRepository, ConfigProvider configProvider) {
    this.slideshowRepository = slideshowRepository;
    this.configProvider = configProvider;
  }

  public List<SlideshowModel> getAllSlideshow() {
    return slideshowRepository.findAll();
  }

  public void saveSlideshow(SlideshowModel slideshowModel) {
    slideshowRepository.save(slideshowModel);
  }

  public Resource loadSlideshowAsResource(String fileName) throws IOException {
    Resource resource = new ClassPathResource("slideshow/" + fileName);

    if (resource.exists() || resource.isReadable()) {
      return resource;
    } else {
      throw new RuntimeException("Could not read file: " + fileName);
    }
  }

  public ResponseEntity<String> addExistingSlideshow() {
    try {
      String slideshowDirectoryPath = System.getenv("SLIDESHOW_FILE_PATH");

      List<Path> slideshowFiles = Files.walk(Paths.get(slideshowDirectoryPath), 1, FileVisitOption.FOLLOW_LINKS)
        .filter(Files::isRegularFile)
        .collect(Collectors.toList());

      for (Path slideshowFilePath : slideshowFiles) {

        String fileName = slideshowFilePath.getFileName().toString();

        if (!slideshowRepository.existsByFilePath("slideshow/" + fileName)) {
          SlideshowModel slideshowModel = new SlideshowModel();
          slideshowModel.setFilePath(fileName);

          String fileExtension = FilenameUtils.getExtension(fileName).toLowerCase();

          slideshowModel.setType(fileExtension);

          saveSlideshow(slideshowModel);
        }
      }
      return ResponseEntity.ok("Existing slideshow files added to database.");
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body("An error occurred while adding existing slideshow.");
    }
  }

  public void deleteSlideshow(Long id) {
    Optional<SlideshowModel> optionalSlideshow = slideshowRepository.findById(id);

    if (optionalSlideshow.isPresent()) {
      SlideshowModel slideshow = optionalSlideshow.get();

      // Delete file from the "slideshow" directory
      String filePath = slideshow.getFilePath();
      File file = new File(configProvider.slideshowDirectory, filePath);

      if (file.exists()) {
        file.delete();
        if (file == null) {
          slideshowRepository.deleteById(id);
          System.out.println("File deleted successfully");
        } else {
          System.err.println("Failed to delete the file");
        }
      } else {
        System.err.println("File not found");
      }

      // Delete the database record
      slideshowRepository.deleteById(id);
    }
  }
}