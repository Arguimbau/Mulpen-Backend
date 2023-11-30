package dk.kea.mulpenbackend.config;

import org.springframework.stereotype.Component;

@Component
public class ConfigProvider {
    public String uploadDirectory = System.getenv("MEDIA_FILE_PATH");
}
