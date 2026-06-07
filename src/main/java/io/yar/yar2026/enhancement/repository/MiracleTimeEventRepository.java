package io.yar.yar2026.enhancement.repository;

import io.yar.yar2026.enhancement.domain.MiracleTimeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MiracleTimeEventRepository extends JpaRepository<MiracleTimeEvent, Long> {

    @Query("""
            select m
            from MiracleTimeEvent m
            where m.active = true
              and m.startAt <= :now
              and m.endAt >= :now
            order by m.startAt desc
            """)
    Optional<MiracleTimeEvent> findActiveEvent(@Param("now") LocalDateTime now);
}
