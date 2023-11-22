package dk.kea.mulpenbackend.repository;

import dk.kea.mulpenbackend.model.MediaAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaAttachmentRepository extends JpaRepository<MediaAttachment, Long> {
}
