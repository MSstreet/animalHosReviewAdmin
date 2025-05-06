package com.example.animalhosreviewadmin.service;

import com.example.animalhosreviewadmin.domain.LoginHistory;
import com.example.animalhosreviewadmin.domain.User;
import com.example.animalhosreviewadmin.repository.LoginHistoryRepository;
import com.example.animalhosreviewadmin.repository.UserRepository;
import com.example.animalhosreviewadmin.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final MfaService mfaService;
    private final LoginHistoryRepository loginHistoryRepository;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES = 30;

    public AuthService(AuthenticationManager authenticationManager,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtTokenProvider tokenProvider,
                      MfaService mfaService,
                      LoginHistoryRepository loginHistoryRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.mfaService = mfaService;
        this.loginHistoryRepository = loginHistoryRepository;
    }

    @Transactional
    public String login(String email, String password, String mfaCode, HttpServletRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 계정 잠금 확인
        if (!user.isAccountNonLocked()) {
            if (user.getLastLoginAttempt().plusMinutes(LOCK_TIME_MINUTES).isAfter(LocalDateTime.now())) {
                throw new LockedException("Account is locked. Try again after " + 
                    user.getLastLoginAttempt().plusMinutes(LOCK_TIME_MINUTES));
            } else {
                // 잠금 시간이 지났으면 계정 잠금 해제
                user.setAccountNonLocked(true);
                user.setLoginAttempts(0);
                userRepository.save(user);
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // MFA 검증
            if (user.isMfaEnabled()) {
                if (mfaCode == null || !mfaService.verifyCode(user.getMfaSecret(), mfaCode)) {
                    throw new RuntimeException("Invalid MFA code");
                }
            }

            // 로그인 성공 처리
            user.setLoginAttempts(0);
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.save(user);

            // 로그인 이력 저장
            saveLoginHistory(user, request, true, null);

            return tokenProvider.generateToken(authentication);
        } catch (AuthenticationException e) {
            // 로그인 실패 처리
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            user.setLastLoginAttempt(LocalDateTime.now());

            if (user.getLoginAttempts() >= MAX_LOGIN_ATTEMPTS) {
                user.setAccountNonLocked(false);
            }

            userRepository.save(user);

            // 로그인 이력 저장
            saveLoginHistory(user, request, false, e.getMessage());

            throw e;
        }
    }

    private void saveLoginHistory(User user, HttpServletRequest request, boolean success, String failureReason) {
        LoginHistory history = new LoginHistory();
        history.setUser(user);
        history.setLoginTime(LocalDateTime.now());
        history.setIpAddress(getClientIp(request));
        history.setUserAgent(request.getHeader("User-Agent"));
        history.setSuccess(success);
        history.setFailureReason(failureReason);
        loginHistoryRepository.save(history);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    @Transactional
    public User register(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_ADMIN");

        return userRepository.save(user);
    }

    @Transactional
    public String setupMfa(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String secret = mfaService.generateNewSecret();
        user.setMfaSecret(secret);
        user.setMfaEnabled(true);
        userRepository.save(user);

        return mfaService.generateQrCodeImageUri(secret, email);
    }

    @Transactional
    public void disableMfa(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userRepository.save(user);
    }
} 