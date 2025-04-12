package proyecto.dh.resources.auth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.auth.dto.AuthenticationRequestDto;
import proyecto.dh.resources.auth.dto.AuthenticationResponseDto;
import proyecto.dh.resources.auth.dto.RefreshTokenRequestDto;
import proyecto.dh.resources.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth Controller", description = "Controlador para ingresar como usuario")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody AuthenticationRequestDto authRequest) throws Exception {
        AuthenticationResponseDto response = authService.authenticate(authRequest);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequest) throws BadRequestException {
        AuthenticationResponseDto response = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(response);
    }
}
