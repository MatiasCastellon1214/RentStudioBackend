package proyecto.dh.resources.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.dh.resources.users.entity.UserAddress;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
}
