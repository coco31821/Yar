package io.yar.yar2026.wallet.domain;

import io.yar.yar2026.common.BaseEntity;
import io.yar.yar2026.user.domain.User;
import io.yar.yar2026.wallet.exception.NotEnoughGoldException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "wallets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private long gold;

    @Column(nullable = false)
    private long gem;

    @Builder
    public Wallet(User user, long gold, long gem) {
        this.user = user;
        this.gold = gold;
        this.gem = gem;
    }

    public static Wallet createDefault(User user) {
        return Wallet.builder()
                .user(user)
                .gold(5000)
                .gem(10)
                .build();
    }

    public void useGold(int amount) {
        if (this.gold < amount) {
            throw new NotEnoughGoldException();
        }

        this.gold -= amount;
    }
}
