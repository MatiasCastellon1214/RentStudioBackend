package proyecto.dh.resources.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyecto.dh.resources.product.entity.CategoryPolicy;
@Repository
public interface CategoryPolicyRepository extends JpaRepository<CategoryPolicy, Long> {
}
