package dk.kea.mulpenbackend.repository;

import dk.kea.mulpenbackend.model.MediaModel;
import dk.kea.mulpenbackend.model.SlideshowModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideshowRepository extends JpaRepository<SlideshowModel, Long> {
  public boolean existsByFilePath(String filepath);

}
