package io.yar.yar2026.npc.domain;

import io.yar.yar2026.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "npcs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Npc extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long npcId;

    @Column(nullable = false, unique = true)
    private String rId;

    @Column(nullable = false)
    private String name;

    private String description;

    private String locationKey;

    @Column(nullable = false)
    private boolean active;

    @Builder
    public Npc(
            String rId,
            String name,
            String description,
            String locationKey,
            Boolean active
    ) {
        this.rId = rId;
        this.name = name;
        this.description = description;
        this.locationKey = locationKey;
        this.active = active == null || active;
    }
}
