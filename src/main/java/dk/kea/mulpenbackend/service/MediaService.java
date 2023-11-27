package dk.kea.mulpenbackend.service;

import dk.kea.mulpenbackend.model.MediaModel;
import dk.kea.mulpenbackend.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class MediaService {
    @Autowired
    MediaRepository mediaRepository;
    public List<MediaModel> getAllMedia() {
        return mediaRepository.findAll();
    }

    public void saveMedia(MediaModel mediaItem) {
        mediaRepository.save(mediaItem);
    }

    public Resource loadMediaAsResource(String fileName) throws IOException {
            Resource resource = new ClassPathResource("media/" + fileName);

            if(resource.exists() || resource.isReadable()){
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + fileName);
            }
    }
}
