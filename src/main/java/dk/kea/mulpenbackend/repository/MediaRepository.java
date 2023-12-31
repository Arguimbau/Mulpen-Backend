package dk.kea.mulpenbackend.repository;

import dk.kea.mulpenbackend.model.MediaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<MediaModel, Long> {
  public boolean existsByFilePath(String filepath);
  public MediaModel findByFilePath(String filepath);
}
