package io.yar.yar2026.user.service;

import io.yar.yar2026.user.domain.User;
import io.yar.yar2026.user.dto.LoginRequest;
import io.yar.yar2026.user.dto.LoginResponse;
import io.yar.yar2026.user.dto.SignupRequest;
import io.yar.yar2026.user.dto.UserCreateResponse;
import io.yar.yar2026.user.exception.DuplicateEmailException;
import io.yar.yar2026.user.exception.LoginFailedException;
import io.yar.yar2026.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(LoginFailedException::new);

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new LoginFailedException();
        }

        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();

        return new LoginResponse(
                accessToken,
                refreshToken,
                900L
        );



    }

}
