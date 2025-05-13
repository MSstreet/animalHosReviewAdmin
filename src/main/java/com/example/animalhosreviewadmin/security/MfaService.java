package com.example.animalhosreviewadmin.security;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class MfaService {
    private static final int TOKEN_LENGTH = 32;
    private static final int TOKEN_VALIDITY_MINUTES = 5;
    private final SecureRandom secureRandom = new SecureRandom();
    private final MfaTokenRepository mfaTokenRepository;

    public MfaService(MfaTokenRepository mfaTokenRepository) {
        this.mfaTokenRepository = mfaTokenRepository;
    }

    @Transactional
    public String generateMfaToken(String username) {
        byte[] randomBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_MINUTES);
        MfaToken mfaToken = new MfaToken(username, token, expiryDate);
        
        mfaTokenRepository.save(mfaToken);
        
        return token;
    }

    public boolean validateMfaToken(String username, String token) {
        return mfaTokenRepository.findByUsernameAndTokenAndUsedFalse(username, token)
                .map(mfaToken -> !mfaToken.getExpiryDate().isBefore(LocalDateTime.now()))
                .orElse(false);
    }

    @Transactional
    public void markTokenAsUsed(String token) {
        mfaTokenRepository.findByUsernameAndTokenAndUsedFalse(null, token)
                .ifPresent(mfaToken -> {
                    mfaToken.setUsed(true);
                    mfaTokenRepository.save(mfaToken);
                });
    }

    @Transactional
    public void cleanupExpiredTokens() {
        mfaTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
} 