package proyecto.dh.resources.auth.service;

import org.springframework.stereotype.Service;
import proyecto.dh.exceptions.handler.BadRequestException;
import proyecto.dh.resources.auth.entity.RefreshToken;
import proyecto.dh.resources.auth.repository.RefreshTokenRepository;

import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken create(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void invalidateRefreshToken(RefreshToken token) {
        token.setValid(false);
        refreshTokenRepository.save(token);
    }
    public RefreshToken verifyExpiration(RefreshToken token) throws BadRequestException {
        if (token.getExpiryDate().before(new java.util.Date()) || !token.isValid()) {
            throw new BadRequestException("Refresh token is invalid or expired");
        }
        return token;
    }
}
