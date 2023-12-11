package dk.kea.mulpenbackend.service;

import dk.kea.mulpenbackend.MulpenBackendApplication;
import dk.kea.mulpenbackend.config.ConfigProvider;
import dk.kea.mulpenbackend.model.MediaModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

  @Test
  void testSpecificFileInMediaDirectory() {
    String testFileName = configProvider.testFileName;
    String filePath = Paths.get(configProvider.uploadDirectory, testFileName).toString();

    System.out.println("Checking file existence: " + filePath);
    assertTrue(Files.exists(Paths.get(filePath)), "File not found: " + testFileName);
  }

  @Test
  void deleteMedia() {
    MediaModel media = new MediaModel();
    String testFileName = configProvider.testFileName;
    media.setFilePath(configProvider.uploadDirectory + "/" + testFileName);
    media.setDescription(configProvider.testFileDescription);
    media.setType(configProvider.fileType);

    mediaService.saveMedia(media);
    Long mediaId = mediaService.getAllMedia().get(0).getId();

    mediaService.deleteMedia(mediaId);

    System.out.println("Media items after deletion: " + mediaService.getAllMedia().size());
    assertTrue(mediaService.getAllMedia().isEmpty());
  }
}