package dk.kea.mulpenbackend.config;

import org.springframework.core.io.Resource;
import dk.kea.mulpenbackend.model.MediaItem;
import dk.kea.mulpenbackend.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class initData implements CommandLineRunner {
    @Autowired
    MediaService mediaService;

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public void run(String... args) throws Exception {
/*
        Resource resource = resourceLoader.getResource("classpath:/media/");
        File mediaDirectory = resource.getFile();
        System.out.println("Classpath for /media/: " + mediaDirectory.getAbsolutePath());

        MediaItem mediaItem = new MediaItem();
        mediaItem.setDescription("red picture");
        mediaItem.setFilePath("media/seele.jpg");
        mediaItem.setType("image");

        mediaService.saveMedia(mediaItem);

 */
        mediaService.addExistingMedia();

    }
}
