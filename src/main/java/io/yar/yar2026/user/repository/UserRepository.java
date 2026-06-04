package io.yar.yar2026.user.repository;

import io.yar.yar2026.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);


}
