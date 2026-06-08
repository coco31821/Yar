package io.yar.yar2026.friend.repository;

import io.yar.yar2026.friend.domain.FriendRequest;
import io.yar.yar2026.friend.domain.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("""
            select case when count(fr) > 0 then true else false end
            from FriendRequest fr
            where (fr.fromUser.userId = :userId and fr.toUser.userId = :targetUserId)
               or (fr.fromUser.userId = :targetUserId and fr.toUser.userId = :userId)
            """)
    boolean existsBetweenUsers(
            @Param("userId") Long userId,
            @Param("targetUserId") Long targetUserId
    );

    List<FriendRequest> findAllByToUser_UserIdAndStatusOrderByCreatedAtDesc(
            Long userId,
            FriendRequestStatus status
    );

    List<FriendRequest> findAllByFromUser_UserIdAndStatusOrderByCreatedAtDesc(
            Long userId,
            FriendRequestStatus status
    );

    @Query("""
            select fr
            from FriendRequest fr
            where (fr.fromUser.userId = :userId or fr.toUser.userId = :userId)
              and fr.status = :status
            order by fr.createdAt desc
            """)
    List<FriendRequest> findAllByUserIdAndStatusOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("status") FriendRequestStatus status
    );

    @Query("""
            select fr
            from FriendRequest fr
            where ((fr.fromUser.userId = :userId and fr.toUser.userId = :friendUserId)
                or (fr.fromUser.userId = :friendUserId and fr.toUser.userId = :userId))
              and fr.status = :status
            """)
    Optional<FriendRequest> findByUserIdAndFriendUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("friendUserId") Long friendUserId,
            @Param("status") FriendRequestStatus status
    );
}
