package dk.kea.mulpenbackend.service;

import dk.kea.mulpenbackend.MulpenBackendApplication;
import dk.kea.mulpenbackend.config.ConfigProvider;
import dk.kea.mulpenbackend.model.SlideshowModel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MulpenBackendApplication.class)
@Transactional
@Rollback
public class SlideshowServiceTest {

  @Autowired
  SlideshowService slideshowService;

  @Autowired
  ConfigProvider configProvider;

  @TempDir
  Path tempDir;

  @BeforeEach
  void beforeEach() {
    try {
      // Create a temporary directory if it doesn't exist
      Path tempDirPath = Files.createDirectories(Paths.get(configProvider.slideshowDirectory));

      // copy files to tempDir (for test purposes)
      Path defaultFilesDir = Paths.get(configProvider.slideshowDirectory);

      // Copy files to tempDir for testing
      Files.walk(defaultFilesDir)
        .filter(Files::isRegularFile)
        .forEach(defaultFile -> {
          try {
            Path targetFile = tempDir.resolve(defaultFilesDir.relativize(defaultFile));
            Files.copy(defaultFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
          } catch (IOException e) {
            System.err.println("Error copying file to tempDir: " + e.getMessage());
          }
        });

      // Copy files to the actual slideshow directory (configProvider.slideshowDirectory)
      Files.walk(defaultFilesDir)
        .filter(Files::isRegularFile)
        .forEach(defaultFile -> {
          try {
            Path targetFile = tempDirPath.resolve(defaultFilesDir.relativize(defaultFile));
            Files.copy(defaultFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
          } catch (IOException e) {
            System.err.println("Error copying file to upload directory: " + e.getMessage());
          }
        });
    } catch (IOException e) {
      System.err.println("Error creating or walking defaultFilesDir: " + e.getMessage());
    }
  }

  @Test
  void getAllSlideshow() {
    List<SlideshowModel> slideshowModel = slideshowService.getAllSlideshow();
    System.out.println("Number of media items: " + slideshowModel.size());
    assertNotNull(slideshowModel);
    assertFalse(slideshowModel.isEmpty());
  }

  @Test
  void saveSlideshow() {
    SlideshowModel slideshow = new SlideshowModel();
    String testFileName = configProvider.testSlideshowFileName;
    slideshow.setFilePath(configProvider.slideshowDirectory + "/" + testFileName);
    slideshow.setType(configProvider.slideshowFileType);

    slideshowService.saveSlideshow(slideshow);

    // Verify that the saved slideshow item exists with the correct properties
    SlideshowModel savedSlideshow = slideshowService.getAllSlideshow().stream()
      .filter(m -> testFileName.equals(Paths.get(m.getFilePath()).getFileName().toString()))
      .findFirst()
      .orElse(null);

    assertNotNull(savedSlideshow);
    System.out.println("Saved slideshow type: " + savedSlideshow.getType());
    assertEquals(configProvider.slideshowFileType, savedSlideshow.getType());
  }

  @Test
  void loadSlideshowAsResource() throws IOException {
    Resource resource = slideshowService.loadSlideshowAsResource(configProvider.testSlideshowFileName);

    assertNotNull(resource);
    System.out.println("Resource exists: " + resource.exists());
    System.out.println("Resource is readable: " + resource.isReadable());
    assertTrue(resource.exists());
    assertTrue(resource.isReadable());
  }

  @Test
  void addExistingSlideshow() {
    ResponseEntity<String> response = slideshowService.addExistingSlideshow();
    System.out.println("Response status code: " + response.getStatusCodeValue());
    System.out.println("Response body: " + response.getBody());
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Existing slideshow files added to database.", response.getBody());
  }

  // check the existence of a specific file in the slideshow directory
  @Test
  void testSpecificFileInSlideshowDirectory() {
    String testFileName = configProvider.testSlideshowFileName;
    Path filePath = Paths.get(configProvider.slideshowDirectory, testFileName);

    System.out.println("Checking file existence: " + filePath);
    assertTrue(Files.exists(filePath), "File not found: " + testFileName);
  }

  @Test
  void deleteSlideshow() {
    String testFileName = configProvider.testSlideshowFileName;
    Path filePath = tempDir.resolve(testFileName);

    System.out.println("Checking file existence: " + filePath);
    assertTrue(Files.exists(filePath), "File not found: " + testFileName);

    // Perform delete
    try {
      Files.delete(filePath);
      System.out.println("File deleted successfully: " + filePath);
    } catch (IOException e) {
      System.err.println("Error deleting file: " + e.getMessage());
      fail("Failed to delete file: " + testFileName);
    }

    // Check if the file still exists
    assertFalse(Files.exists(filePath), "File should not exist after deletion: " + testFileName);
  }
}