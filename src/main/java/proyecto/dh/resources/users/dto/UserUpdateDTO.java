package proyecto.dh.resources.users.dto;

import lombok.Data;
import proyecto.dh.common.enums.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserUpdateDTO {

    @Size(min = 2, max = 30, message = "El nombre debe tener entre 2 y 30 caracteres")
    private String firstName;

    @Size(min = 2, max = 30, message = "El apellido debe tener entre 2 y 30 caracteres")
    private String lastName;

    @Pattern(regexp = "\\d{10}", message = "El teléfono debe ser un número de 10 dígitos")
    private String phone;

    private UserAddressDTO address;

    @Email(message = "El correo electrónico debe ser válido")
    private String email;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    private Role role;

}
