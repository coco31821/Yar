package io.yar.yar2026.profile.repository;

import io.yar.yar2026.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser_UserId(Long userId);
}
