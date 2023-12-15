package dk.kea.mulpenbackend.config;

import org.springframework.stereotype.Component;

@Component
public class ConfigProvider {
    public String uploadDirectory = System.getenv("MEDIA_FILE_PATH");
    public String slideshowDirectory = System.getenv("SLIDESHOW_FILE_PATH");
    public String profileDirectory = System.getenv("PROFILE_FILE_PATH");
    public String thumbnailDirectory = System.getenv("MEDIA_FILE_PATH") + "/thumbnails";
}
