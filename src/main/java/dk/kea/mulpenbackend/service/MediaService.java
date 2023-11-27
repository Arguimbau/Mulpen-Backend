package dk.kea.mulpenbackend.service;

import dk.kea.mulpenbackend.model.MediaItem;
import dk.kea.mulpenbackend.repository.MediaRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.tika.Tika;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaService {

    MediaRepository mediaRepository;

    @Autowired
    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public List<MediaItem> getAllMedia() {
        return mediaRepository.findAll();
    }

    public void saveMedia(MediaItem mediaItem) {
        mediaRepository.save(mediaItem);
    }

    public Resource loadMediaAsResource(String fileName) throws IOException {
        Resource resource = new ClassPathResource("media/" + fileName);

        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Could not read file: " + fileName);
        }
    }

    public ResponseEntity<String> addExistingMedia() {
        try {
            String mediaDirectoryPath = System.getenv("MEDIA_FILE_PATH");

            List<Path> mediaFiles = Files.walk(Paths.get(mediaDirectoryPath), 1, FileVisitOption.FOLLOW_LINKS)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            Tika tika = new Tika();

            for (Path mediaFilePath : mediaFiles) {
                String fileName = mediaFilePath.getFileName().toString();

                if (!mediaRepository.existsByFilePath("media/" + fileName)) {
                    MediaItem mediaItem = new MediaItem();
                    mediaItem.setFilePath("media/" + fileName);
                    mediaItem.setDescription("Description for " + fileName);

                    String fileExtension = FilenameUtils.getExtension(fileName).toLowerCase();

                    mediaItem.setType(fileExtension);

                    saveMedia(mediaItem);
                }
            }
            return ResponseEntity.ok("Existing media files added to database.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred while adding existing media.");
        }
    }
}
