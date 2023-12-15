package dk.kea.mulpenbackend.config;

import org.springframework.stereotype.Component;

@Component
public class ConfigProvider {
    public String uploadDirectory = System.getenv("MEDIA_FILE_PATH");
    public String slideshowDirectory = System.getenv("SLIDESHOW_FILE_PATH");
    public String profileDirectory = System.getenv("PROFILE_FILE_PATH");
    public String thumbnailDirectory = System.getenv("MEDIA_FILE_PATH") + "/thumbnails";

  // dynamic attributes for test media service
  public String testMediaFileName = System.getenv("TEST_MEDIA_FILE_NAME");
  public String mediaFileType = System.getenv("MEDIA_FILE_TYPE");
  public String testFileDescription = System.getenv("TEST_FILE_DESCRIPTION");

  public String testSlideshowFileName = System.getenv("TEST_SLIDESHOW_FILE_NAME");

  public String slideshowFileType = System.getenv("SLIDESHOW_FILE_TYPE");

  // dynamic for test filename
  public String getTestMediaFileName() {
    return testMediaFileName + "." + mediaFileType;
  }

  public String getTestFileDescription() {
    return testFileDescription;
  }

  public String getTestSlideshowFileName() {
    return testSlideshowFileName + "." + slideshowFileType;
  }
}
