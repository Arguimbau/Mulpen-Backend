package dk.kea.mulpenbackend.service;

import dk.kea.mulpenbackend.config.ConfigProvider;
import dk.kea.mulpenbackend.Entity.MediaModel;
import dk.kea.mulpenbackend.Repository.MediaRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class MediaService {

    MediaRepository mediaRepository;
    private ConfigProvider configProvider;

    @Autowired
    public MediaService(MediaRepository mediaRepository, ConfigProvider configProvider) {
        this.mediaRepository = mediaRepository;
        this.configProvider = configProvider;
    }

    public List<MediaModel> getAllMedia() {
        return mediaRepository.findAll();
    }

    public void saveMedia(MediaModel mediaItem) {
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

            for (Path mediaFilePath : mediaFiles) {

                String fileName = mediaFilePath.getFileName().toString();

                if (!mediaRepository.existsByFilePath("media/" + fileName)) {
                    MediaModel media = new MediaModel();
                    media.setFilePath(fileName);
                    media.setDescription("Description for " + fileName);

                    String fileExtension = FilenameUtils.getExtension(fileName).toLowerCase();

                    media.setType(fileExtension);

                    saveMedia(media);
                }
            }
            return ResponseEntity.ok("Existing media files added to database.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred while adding existing media.");
        }
    }

    public void deleteMedia(Long id) {
        Optional<MediaModel> optionalMedia = mediaRepository.findById(id);

        if (optionalMedia.isPresent()) {
            MediaModel media = optionalMedia.get();

            // Delete file from the "media" directory
            String filePath = media.getFilePath();
            File file = new File(configProvider.uploadDirectory, filePath);

            if (file.exists()) {
                file.delete();
                if (file == null) {
                    mediaRepository.deleteById(id);
                    System.out.println("File deleted successfully");
                } else {
                    System.err.println("Failed to delete the file");
                }
            } else {
                System.err.println("File not found");
            }

            // Delete the database record
            mediaRepository.deleteById(id);
        }
    }
}
