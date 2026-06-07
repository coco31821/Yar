package io.yar.yar2026.enhancement.repository;

import io.yar.yar2026.enhancement.domain.EnhancementRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnhancementRuleRepository extends JpaRepository<EnhancementRule, Long> {

    Optional<EnhancementRule> findByFromGradeAndActiveTrue(int fromGrade);
}

