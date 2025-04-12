package proyecto.dh.resources.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.dh.resources.auth.entity.RefreshToken;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}
