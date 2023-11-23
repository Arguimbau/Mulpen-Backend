package dk.kea.mulpenbackend.service;

import dk.kea.mulpenbackend.model.MediaItem;
import dk.kea.mulpenbackend.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaService {
    @Autowired
    MediaRepository mediaRepository;
    public List<MediaItem> getAllMedia() {
        return mediaRepository.findAll();
    }

    public void saveMedia(MediaItem mediaItem) {
        mediaRepository.save(mediaItem);
    }
}
