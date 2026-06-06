package io.yar.yar2026.friend.domain;

import io.yar.yar2026.common.BaseEntity;
import io.yar.yar2026.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "friend_requests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_friend_request_users",
                        columnNames = {"from_user_id", "to_user_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendRequestStatus status;

    @Builder
    public FriendRequest(User fromUser, User toUser, FriendRequestStatus status) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.status = status == null ? FriendRequestStatus.PENDING : status;
    }

    public void accept() {
        this.status = FriendRequestStatus.ACCEPTED;
    }

    public void decline() {
        this.status = FriendRequestStatus.DECLINED;
    }

    public void cancel() {
        this.status = FriendRequestStatus.CANCELED;
    }
}
