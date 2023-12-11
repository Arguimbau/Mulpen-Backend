package dk.kea.mulpenbackend.service;

import dk.kea.mulpenbackend.MulpenBackendApplication;
import dk.kea.mulpenbackend.config.ConfigProvider;
import dk.kea.mulpenbackend.model.MediaModel;
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
public class MediaServiceTest {

  @Autowired
  MediaService mediaService;

  @Autowired
  ConfigProvider configProvider;

  @TempDir
  Path tempDir;

  @BeforeEach
  void beforeEach() {
    try {
      // Create a temporary directory if it doesn't exist
      Path tempDirPath = Files.createDirectories(Paths.get(configProvider.uploadDirectory));

      // copy files to tempDir (for test purposes)
      Path defaultFilesDir = Paths.get(configProvider.uploadDirectory);

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

      // Copy files to the actual upload directory (configProvider.uploadDirectory)
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
  void getAllMedia() {
    List<MediaModel> mediaModel = mediaService.getAllMedia();
    System.out.println("Number of media items: " + mediaModel.size());
    assertNotNull(mediaModel);
    assertFalse(mediaModel.isEmpty());
  }

  @Test
  void saveMedia() {
    MediaModel media = new MediaModel();
    String testFileName = configProvider.testFileName;
    media.setFilePath(configProvider.uploadDirectory + "/" + testFileName);
    media.setDescription(configProvider.testFileDescription);
    media.setType(configProvider.fileType);

    mediaService.saveMedia(media);

    // Verify that the saved media item exists with the correct properties
    MediaModel savedMedia = mediaService.getAllMedia().stream()
      .filter(m -> testFileName.equals(Paths.get(m.getFilePath()).getFileName().toString()))
      .findFirst()
      .orElse(null);

    assertNotNull(savedMedia);
    System.out.println("Saved media description: " + savedMedia.getDescription());
    System.out.println("Saved media type: " + savedMedia.getType());
    assertEquals(configProvider.testFileDescription, savedMedia.getDescription());
    assertEquals(configProvider.fileType, savedMedia.getType());
  }

  @Test
  void loadMediaAsResource() throws IOException {
    Resource resource = mediaService.loadMediaAsResource(configProvider.testFileName);

    assertNotNull(resource);
    System.out.println("Resource exists: " + resource.exists());
    System.out.println("Resource is readable: " + resource.isReadable());
    assertTrue(resource.exists());
    assertTrue(resource.isReadable());
  }

  @Test
  void addExistingMedia() {
    ResponseEntity<String> response = mediaService.addExistingMedia();
    System.out.println("Response status code: " + response.getStatusCodeValue());
    System.out.println("Response body: " + response.getBody());
    assertEquals(200, response.getStatusCodeValue());
    assertEquals("Existing media files added to database.", response.getBody());
  }

  // check the existence of a specific file in the media directory
  @Test
  void testSpecificFileInMediaDirectory() {
    String testFileName = configProvider.testFileName;
    Path filePath = Paths.get(configProvider.uploadDirectory, testFileName);

    System.out.println("Checking file existence: " + filePath);
    assertTrue(Files.exists(filePath), "File not found: " + testFileName);
  }

  @Test
  void testDeleteMedia() {
    String testFileName = configProvider.testFileName;
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