package dk.kea.mulpenbackend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class ConfigProvider {
  public String uploadDirectory = System.getenv("MEDIA_FILE_PATH");
  public String slideshowDirectory = System.getenv("SLIDESHOW_FILE_PATH");
  public String profileDirectory = System.getenv("PROFILE_FILE_PATH");

  // dynamic attributes for test media service
  public String testFileName = System.getenv("TEST_FILE_NAME");
  public String fileType = System.getenv("MEDIA_FILE_TYPE");
  public String testFileDescription = System.getenv("TEST_FILE_DESCRIPTION");


  // dynamic for test filename
  public String getTestFileName() {
    return testFileName + "." + fileType;
  }

  public String getTestFileDescription() {
    return testFileDescription;
  }
}
