package dk.kea.mulpenbackend.repository;

import dk.kea.mulpenbackend.model.MediaPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaPostRepository extends JpaRepository<MediaPost, Long> {
}
