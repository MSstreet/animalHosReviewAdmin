package com.example.animalhosreviewadmin.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MfaTokenRepository extends JpaRepository<MfaToken, Long> {
    Optional<MfaToken> findByUsernameAndTokenAndUsedFalse(String username, String token);
    void deleteByExpiryDateBefore(java.time.LocalDateTime date);
} 