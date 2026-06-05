package io.yar.yar2026.user.service;

import io.yar.yar2026.common.config.security.TokenProvider;
import io.yar.yar2026.user.domain.RefreshToken;
import io.yar.yar2026.user.domain.User;
import io.yar.yar2026.user.dto.*;
import io.yar.yar2026.user.exception.DuplicateEmailException;
import io.yar.yar2026.user.exception.LoginFailedException;
import io.yar.yar2026.user.repository.RefreshTokenRepository;
import io.yar.yar2026.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;


    // 회원가입
    @Transactional
    public UserCreateResponse signup(SignupRequest request) {

        String email = request.email().trim();

        System.out.println("===== 회원가입 요청 =====");
        System.out.println("요청 email = [" + email + "]");
        System.out.println("중복 여부 = " + userRepository.existsByEmail(email));


        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = User.builder()
                .email(request.email())
                .password(encodedPassword)
                .nickname(request.nickname())
                .build();

        User savedUser = userRepository.save(user);

        return UserCreateResponse.from(savedUser);
    }

    // 로그인
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(LoginFailedException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new LoginFailedException();
        }

        user.updateLastLoginAt();

        String accessToken = tokenProvider.issueAccessToken(
                user.getUserId(),
                user.getRole()
        );

        String refreshToken = UUID.randomUUID().toString();

        LocalDateTime refreshTokenExpiredAt = LocalDateTime.now()
                .plusSeconds(tokenProvider.getRefreshTokenExpirationSeconds());

        refreshTokenRepository.save(
                RefreshToken.create(user, refreshToken, refreshTokenExpiredAt)
        );

        return new LoginResponse(
                accessToken,
                refreshToken,
                tokenProvider.getAccessTokenExpirationSeconds()
        );
    }

}
