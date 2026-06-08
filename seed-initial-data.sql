-- MySQL seed data based on Unity_API_명세서_강화추가.xlsx examples.
-- Run this after the Spring app has created/updated tables.
--
-- This file intentionally does NOT insert rows into users.
-- Create users through the signup API first, then rerun this file to attach
-- profile, wallet, inventory, and friend request data to those users.
--
-- Example signup users used by the optional user-related seed section:
--   gamer@test.com     / mypassword123
--   friend@test.com    / mypassword123
--   requester@test.com / mypassword123

CREATE DATABASE IF NOT EXISTS yar;
USE yar;

SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------------
-- Master data: items
-- Based on INF_UNITY_008, INF_UNITY_009, INF_UNITY_021, INF_UNITY_030.
-- ---------------------------------------------------------------------------
INSERT IGNORE INTO items (
    item_id,
    r_id,
    item_name,
    item_type,
    item_grade,
    description,
    price,
    sell_price,
    created_at,
    updated_at
) VALUES
      (1, 'sword_001', '연습용 검', 'WEAPON', 'COMMON', '초보자용 검입니다.', 100, 50, NOW(), NOW()),
      (2, 'potion_hp_001', 'HP 포션', 'CONSUMABLE', 'COMMON', 'HP를 50 회복합니다.', 30, 10, NOW(), NOW()),
      (3, 'wooden_bow', '나무 활', 'WEAPON', 'COMMON', '초보자용 활이다.', 200, 100, NOW(), NOW()),
      (4, 'potion_mp_001', 'MP 포션', 'CONSUMABLE', 'COMMON', 'MP를 30 회복합니다.', 30, 10, NOW(), NOW()),
      (5, 'armor_001', '가죽 갑옷', 'ARMOR', 'COMMON', '가벼운 가죽 갑옷입니다.', 150, 75, NOW(), NOW()),
      (6, 'sword_002', '강철 검', 'WEAPON', 'UNCOMMON', '단단한 강철로 만든 검입니다.', 300, 150, NOW(), NOW()),
      (7, 'armor_002', '철 갑옷', 'ARMOR', 'UNCOMMON', '방어력이 좋은 철 갑옷입니다.', 400, 200, NOW(), NOW()),
      (8, 'gem_green_001', '초록 보석', 'ETC', 'RARE', '특별한 기운이 담긴 보석입니다.', 500, 250, NOW(), NOW()),
      (9, 'ticket_miracle_001', '미라클 티켓', 'ETC', 'EPIC', '미라클 타임 이벤트를 기념하는 티켓입니다.', 1000, 500, NOW(), NOW());

-- ---------------------------------------------------------------------------
-- Master data: NPC and shop
-- Based on INF_UNITY_025, INF_UNITY_026, INF_UNITY_027.
-- ---------------------------------------------------------------------------
INSERT IGNORE INTO npcs (
    npc_id,
    r_id,
    name,
    description,
    location_key,
    active,
    created_at,
    updated_at
) VALUES
      (1, 'npc_merchant_001', '상인 밥', '마을 입구의 상인입니다.', 'village_entrance', TRUE, NOW(), NOW()),
      (2, 'npc_enhance_001', '강화 장인', '아이템 강화를 진행하는 NPC입니다.', 'village_forge', TRUE, NOW(), NOW());

INSERT IGNORE INTO npc_sale_items (
    npc_sale_item_id,
    npc_id,
    item_id,
    price,
    currency_type,
    stock_quantity,
    sort_order,
    active,
    sale_start_at,
    sale_end_at,
    created_at,
    updated_at
) VALUES
      (1, 1, 2, 30, 'GOLD', 99, 1, TRUE, NULL, NULL, NOW(), NOW()),
      (2, 1, 4, 30, 'GOLD', 99, 2, TRUE, NULL, NULL, NOW(), NOW()),
      (3, 1, 1, 100, 'GOLD', 10, 3, TRUE, NULL, NULL, NOW(), NOW()),
      (4, 1, 3, 200, 'GOLD', 10, 4, TRUE, NULL, NULL, NOW(), NOW()),
      (5, 1, 5, 150, 'GOLD', 10, 5, TRUE, NULL, NULL, NOW(), NOW());

-- ---------------------------------------------------------------------------
-- Master data: enhancement
-- Based on INF_UNITY_029 and INF_UNITY_030.
-- Grade 0 -> 1 intentionally matches the spec example:
-- successRate 70, failRate 30, destroyRate 0, goldCost 100.
-- ---------------------------------------------------------------------------
INSERT IGNORE INTO enhancement_rules (
    enhancement_rule_id,
    from_grade,
    to_grade,
    success_rate,
    fail_rate,
    destroy_rate,
    gold_cost,
    active,
    created_at,
    updated_at
) VALUES
      (1, 0, 1, 70, 30, 0, 100, TRUE, NOW(), NOW()),
      (2, 1, 2, 65, 35, 0, 200, TRUE, NOW(), NOW()),
      (3, 2, 3, 60, 40, 0, 300, TRUE, NOW(), NOW()),
      (4, 3, 4, 55, 40, 5, 400, TRUE, NOW(), NOW()),
      (5, 4, 5, 50, 40, 10, 500, TRUE, NOW(), NOW()),
      (6, 5, 6, 45, 40, 15, 800, TRUE, NOW(), NOW()),
      (7, 6, 7, 40, 40, 20, 1000, TRUE, NOW(), NOW()),
      (8, 7, 8, 35, 40, 25, 1200, TRUE, NOW(), NOW()),
      (9, 8, 9, 30, 40, 30, 1500, TRUE, NOW(), NOW()),
      (10, 9, 10, 25, 40, 35, 2000, TRUE, NOW(), NOW()),
      (11, 10, 11, 20, 40, 40, 2500, TRUE, NOW(), NOW()),
      (12, 11, 12, 18, 42, 40, 3000, TRUE, NOW(), NOW()),
      (13, 12, 13, 16, 44, 40, 3500, TRUE, NOW(), NOW()),
      (14, 13, 14, 14, 46, 40, 4000, TRUE, NOW(), NOW()),
      (15, 14, 15, 12, 48, 40, 4500, TRUE, NOW(), NOW()),
      (16, 15, 16, 10, 50, 40, 5000, TRUE, NOW(), NOW()),
      (17, 16, 17, 8, 52, 40, 6000, TRUE, NOW(), NOW()),
      (18, 17, 18, 6, 54, 40, 7000, TRUE, NOW(), NOW()),
      (19, 18, 19, 5, 55, 40, 8000, TRUE, NOW(), NOW()),
      (20, 19, 20, 5, 55, 40, 10000, TRUE, NOW(), NOW());

INSERT IGNORE INTO miracle_time_events (
    miracle_time_event_id,
    name,
    start_at,
    end_at,
    bonus_grade_step,
    notice_message,
    active,
    created_at,
    updated_at
) VALUES
      (1, '상시 테스트 미라클 타임', '2026-01-01 00:00:00', '2026-12-31 23:59:59', 2, '미라클 타임 진행 중! 강화 성공 시 등급이 2배로 상승합니다.', TRUE, NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------------
-- User-related seed.
-- This section inserts nothing until matching users exist.
-- After signup, rerun this file. It looks up user_id by email.
-- ---------------------------------------------------------------------------
SET @gamer_user_id = (SELECT user_id FROM users WHERE email = 'gamer@test.com' LIMIT 1);
SET @friend_user_id = (SELECT user_id FROM users WHERE email = 'friend@test.com' LIMIT 1);
SET @requester_user_id = (SELECT user_id FROM users WHERE email = 'requester@test.com' LIMIT 1);

-- Based on INF_UNITY_007, INF_UNITY_023.
INSERT IGNORE INTO profiles (
    user_id,
    level,
    exp,
    total_play_seconds,
    created_at,
    updated_at
)
SELECT @gamer_user_id, 10, 500, 7200, '2025-01-01 00:00:00', NOW()
WHERE @gamer_user_id IS NOT NULL;

INSERT IGNORE INTO profiles (
    user_id,
    level,
    exp,
    total_play_seconds,
    created_at,
    updated_at
)
SELECT @friend_user_id, 1, 0, 0, NOW(), NOW()
WHERE @friend_user_id IS NOT NULL;

INSERT IGNORE INTO profiles (
    user_id,
    level,
    exp,
    total_play_seconds,
    created_at,
    updated_at
)
SELECT @requester_user_id, 3, 150, 1800, NOW(), NOW()
WHERE @requester_user_id IS NOT NULL;

-- Based on INF_UNITY_007, INF_UNITY_024, INF_UNITY_027.
INSERT IGNORE INTO wallets (
    user_id,
    gold,
    gem,
    created_at,
    updated_at
)
SELECT @gamer_user_id, 5000, 10, NOW(), NOW()
WHERE @gamer_user_id IS NOT NULL;

INSERT IGNORE INTO wallets (
    user_id,
    gold,
    gem,
    created_at,
    updated_at
)
SELECT @friend_user_id, 5000, 10, NOW(), NOW()
WHERE @friend_user_id IS NOT NULL;

INSERT IGNORE INTO wallets (
    user_id,
    gold,
    gem,
    created_at,
    updated_at
)
SELECT @requester_user_id, 5000, 10, NOW(), NOW()
WHERE @requester_user_id IS NOT NULL;

-- Based on INF_UNITY_007, INF_UNITY_017, INF_UNITY_021, INF_UNITY_030.
-- user_item_id is not forced so it remains safe with AUTO_INCREMENT after signup.
INSERT IGNORE INTO user_items (
    user_id,
    item_id,
    quantity,
    status,
    enhancement_grade,
    enhancement_count,
    obtained_from,
    acquired_at,
    destroyed_at,
    created_at,
    updated_at
)
SELECT @gamer_user_id, 1, 1, 'EQUIPPED', 0, 0, 'ADMIN', '2025-01-01 00:00:00', NULL, '2025-01-01 00:00:00', NOW()
WHERE @gamer_user_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM user_items
      WHERE user_id = @gamer_user_id
        AND item_id = 1
        AND enhancement_grade = 0
        AND status = 'EQUIPPED'
  );

INSERT IGNORE INTO user_items (
    user_id,
    item_id,
    quantity,
    status,
    enhancement_grade,
    enhancement_count,
    obtained_from,
    acquired_at,
    destroyed_at,
    created_at,
    updated_at
)
SELECT @gamer_user_id, 2, 5, 'OWNED', 0, 0, 'PICKUP', '2026-05-21 12:00:00', NULL, '2026-05-21 12:00:00', NOW()
WHERE @gamer_user_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM user_items
      WHERE user_id = @gamer_user_id
        AND item_id = 2
        AND enhancement_grade = 0
        AND status = 'OWNED'
  );

INSERT IGNORE INTO user_items (
    user_id,
    item_id,
    quantity,
    status,
    enhancement_grade,
    enhancement_count,
    obtained_from,
    acquired_at,
    destroyed_at,
    created_at,
    updated_at
)
SELECT @gamer_user_id, 3, 1, 'OWNED', 0, 0, 'ADMIN', '2026-06-08 10:00:00', NULL, '2026-06-08 10:00:00', NOW()
WHERE @gamer_user_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM user_items
      WHERE user_id = @gamer_user_id
        AND item_id = 3
        AND enhancement_grade = 0
        AND status = 'OWNED'
  );

-- Based on INF_UNITY_010 through INF_UNITY_013.
INSERT IGNORE INTO friend_requests (
    from_user_id,
    to_user_id,
    status,
    created_at,
    updated_at
)
SELECT @gamer_user_id, @friend_user_id, 'ACCEPTED', '2025-01-10 12:00:00', NOW()
WHERE @gamer_user_id IS NOT NULL
  AND @friend_user_id IS NOT NULL;

INSERT IGNORE INTO friend_requests (
    from_user_id,
    to_user_id,
    status,
    created_at,
    updated_at
)
SELECT @requester_user_id, @gamer_user_id, 'PENDING', '2026-05-21 09:00:00', NOW()
WHERE @requester_user_id IS NOT NULL
  AND @gamer_user_id IS NOT NULL;
