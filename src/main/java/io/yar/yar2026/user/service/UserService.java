package io.yar.yar2026.user.service;

import io.yar.yar2026.common.config.security.TokenProvider;
import io.yar.yar2026.inventory.domain.UserItemStatus;
import io.yar.yar2026.inventory.dto.UserItemResponse;
import io.yar.yar2026.inventory.repository.UserItemRepository;
import io.yar.yar2026.profile.domain.Profile;
import io.yar.yar2026.profile.dto.ProfileResponse;
import io.yar.yar2026.profile.repository.ProfileRepository;
import io.yar.yar2026.user.domain.RefreshToken;
import io.yar.yar2026.user.domain.User;
import io.yar.yar2026.user.dto.*;
import io.yar.yar2026.user.exception.DuplicateEmailException;
import io.yar.yar2026.user.exception.InvalidRefreshTokenException;
import io.yar.yar2026.user.exception.LoginFailedException;
import io.yar.yar2026.user.exception.UserNotFoundException;
import io.yar.yar2026.user.repository.RefreshTokenRepository;
import io.yar.yar2026.user.repository.UserRepository;
import io.yar.yar2026.wallet.domain.Wallet;
import io.yar.yar2026.wallet.dto.WalletResponse;
import io.yar.yar2026.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ProfileRepository profileRepository;
    private final WalletRepository walletRepository;
    private final UserItemRepository userItemRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;


    // 회원가입
    @Transactional
    public UserCreateResponse signup(SignupRequest request) {

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

    // 내 계정 정보 조회
    public UserResponse getMe(Long userId) {
        User user = requireExists(userId);

        return UserResponse.from(user);
    }

    // 내 전체 데이터 조회
    @Transactional
    public UserGameDataResponse getMyData(Long userId) {
        User user = requireExists(userId);
        Profile profile = getOrCreateProfile(user);
        Wallet wallet = getOrCreateWallet(user);

        List<UserItemResponse> inventory = userItemRepository
                .findAllByUser_UserIdAndStatusIn(
                        userId,
                        List.of(UserItemStatus.OWNED, UserItemStatus.EQUIPPED)
                )
                .stream()
                .map(UserItemResponse::from)
                .toList();

        return new UserGameDataResponse(
                UserResponse.from(user),
                ProfileResponse.from(profile),
                WalletResponse.from(wallet),
                inventory,
                0L
        );
    }

    @Transactional
    public ProfileResponse getProfile(Long userId) {
        User user = requireExists(userId);
        Profile profile = getOrCreateProfile(user);

        return ProfileResponse.from(profile);
    }

    public User requireExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    // 로그인
    @Transactional
    public TokenResponse login(LoginRequest request) {
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

        return new TokenResponse(
                accessToken,
                refreshToken,
                tokenProvider.getAccessTokenExpirationSeconds()
        );
    }

    // refreshToken 재발급
    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(InvalidRefreshTokenException::new);

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidRefreshTokenException();
        }

        User user = refreshToken.getUser();

        String newAccessToken = tokenProvider.issueAccessToken(
                user.getUserId(),
                user.getRole()
        );

        String newRefreshToken = UUID.randomUUID().toString();

        LocalDateTime newRefreshTokenExpiredAt = LocalDateTime.now()
                .plusSeconds(tokenProvider.getRefreshTokenExpirationSeconds());

        refreshToken.rotate(newRefreshToken, newRefreshTokenExpiredAt);

        return new TokenResponse(
                newAccessToken,
                newRefreshToken,
                tokenProvider.getAccessTokenExpirationSeconds()
        );
    }

    // logout
    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteAllByUser_UserId(userId);
    }

    private Profile getOrCreateProfile(User user) {
        return profileRepository.findByUser_UserId(user.getUserId())
                .orElseGet(() -> profileRepository.save(Profile.createDefault(user)));
    }

    private Wallet getOrCreateWallet(User user) {
        return walletRepository.findByUser_UserId(user.getUserId())
                .orElseGet(() -> walletRepository.save(Wallet.createDefault(user)));
    }

}
