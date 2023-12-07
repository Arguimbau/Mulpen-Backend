package dk.kea.mulpenbackend.service;

import dk.kea.mulpenbackend.model.MediaModel;
import dk.kea.mulpenbackend.repository.SlideshowRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class SlideshowService {

  SlideshowRepository slideshowRepository;

  @Autowired
  public SlideshowService(SlideshowRepository slideshowRepository) {
    this.slideshowRepository = slideshowRepository;
  }

  public List<MediaModel> getAllSlideshow() {
    return slideshowRepository.findAll();
  }

  public void saveSlideshow(MediaModel mediaItem) {
    slideshowRepository.save(mediaItem);
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
          MediaModel media = new MediaModel();
          media.setFilePath(fileName);

          String fileExtension = FilenameUtils.getExtension(fileName).toLowerCase();

          media.setType(fileExtension);

          saveSlideshow(media);
        }
      }
      return ResponseEntity.ok("Existing media files added to database.");
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body("An error occurred while adding existing media.");
    }
  }
}