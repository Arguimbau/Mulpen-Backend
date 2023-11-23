package dk.kea.mulpenbackend.repository;

import dk.kea.mulpenbackend.model.MediaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<MediaItem, Long> {
}
