package io.yar.yar2026.friend.repository;

import io.yar.yar2026.friend.domain.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
