package proyecto.dh.resources.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.dh.resources.favorite.entity.ProductFavorite;
import proyecto.dh.resources.users.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductFavoriteRepository extends JpaRepository<ProductFavorite, Long> {
    List<ProductFavorite> findByUser(User user);
    Optional<ProductFavorite> findByUserIdAndProductId(Long userId, Long productId);
}
