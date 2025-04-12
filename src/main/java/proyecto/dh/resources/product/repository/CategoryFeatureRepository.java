package proyecto.dh.resources.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.dh.resources.product.entity.CategoryFeature;
@Repository
public interface CategoryFeatureRepository extends JpaRepository<CategoryFeature, Long> {
}
