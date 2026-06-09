package io.yar.yar2026.wallet.repository;

import io.yar.yar2026.wallet.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser_UserId(Long userId);
}
