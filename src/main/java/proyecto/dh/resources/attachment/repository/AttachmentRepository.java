package proyecto.dh.resources.attachment.repository;

import proyecto.dh.resources.attachment.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
