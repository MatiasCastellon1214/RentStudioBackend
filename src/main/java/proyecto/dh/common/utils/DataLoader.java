package proyecto.dh.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import proyecto.dh.common.enums.Role;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.repository.UserRepository;

import java.util.Optional;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {
        createAdminUser();
    }

    private void createAdminUser() {
        String adminEmail = "admin@admin.com";
        Optional<User> findAdmin = userRepository.findByEmail(adminEmail);
        if (findAdmin.isPresent()) {
            userRepository.delete(findAdmin.get());
            System.out.println("[DATA-LOADER] Admin User deleted: " + findAdmin);
        }
        User admin = new User();
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("admin")); // Encripta la contraseña
        admin.setRole(Role.ROLE_ADMIN);
        admin.setFirstName("Admin");
        admin.setLastName("User");

        userRepository.save(admin);
        System.out.println("[DATA-LOADER] Admin user created: " + admin);
    }
}