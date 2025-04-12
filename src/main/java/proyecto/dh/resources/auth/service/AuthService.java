package proyecto.dh.resources.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.auth.dto.AuthenticationRequestDto;
import proyecto.dh.resources.auth.dto.AuthenticationResponseDto;
import proyecto.dh.resources.auth.dto.RefreshTokenRequestDto;
import proyecto.dh.resources.auth.entity.RefreshToken;
import proyecto.dh.resources.auth.jwt.JwtTokenProvider;
import proyecto.dh.resources.users.entity.User;
import proyecto.dh.resources.users.repository.UserRepository;

import java.util.Date;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepository userRepository, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse(null);
        String jwt = jwtTokenProvider.generateToken(userDetails.getUsername(), role);
        String refreshTokenJwt = jwtTokenProvider.generateRefreshToken(userDetails.getUsername());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenJwt);
        refreshToken.setEmail(userDetails.getUsername());
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + jwtTokenProvider.getJwtRefreshExpirationInMillis()));
        refreshToken.setValid(true);

        refreshTokenService.create(refreshToken);

        return new AuthenticationResponseDto(jwt, refreshTokenJwt);
    }

    public AuthenticationResponseDto refreshToken(RefreshTokenRequestDto tokenRefreshRequest) throws BadRequestException {
        String oldRefreshToken = tokenRefreshRequest.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.verifyExpiration(
                refreshTokenService.findByToken(oldRefreshToken).orElseThrow(() -> new RuntimeException("Invalid refresh token"))
        );

        String email = refreshToken.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String role = user.getRole().name();

        String newAccessToken = jwtTokenProvider.generateToken(email, role);
        String newRefreshTokenJwt = jwtTokenProvider.generateRefreshToken(email);

        refreshTokenService.invalidateRefreshToken(refreshToken);

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken(newRefreshTokenJwt);
        newRefreshToken.setEmail(email);
        newRefreshToken.setExpiryDate(new Date(System.currentTimeMillis() + jwtTokenProvider.getJwtRefreshExpirationInMillis()));
        newRefreshToken.setValid(true);

        refreshTokenService.create(newRefreshToken);

        return new AuthenticationResponseDto(newAccessToken, newRefreshTokenJwt);
    }
}