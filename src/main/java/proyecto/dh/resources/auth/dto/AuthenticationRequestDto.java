package proyecto.dh.resources.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Tag(name = "Login")
public class AuthenticationRequestDto {
    private String email;
    private String password;
}
