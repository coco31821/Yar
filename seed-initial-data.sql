-- MySQL seed data for local Unity backend testing.
-- Login test account:
--   email: gamer@test.com
--   password: password

use yar;

SET FOREIGN_KEY_CHECKS = 0;

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
      (1, 'sword_001', '초보자 검', 'WEAPON', 'COMMON', '초보자용 검입니다.', 100, 50, NOW(), NOW()),
      (2, 'sword_002', '강철 검', 'WEAPON', 'UNCOMMON', '단단한 강철로 만든 검입니다.', 300, 150, NOW(), NOW()),
      (3, 'wooden_bow', '나무 활', 'WEAPON', 'COMMON', '초보자용 활입니다.', 200, 100, NOW(), NOW()),
      (4, 'staff_001', '수련 지팡이', 'WEAPON', 'COMMON', '마법 수련용 지팡이입니다.', 180, 90, NOW(), NOW()),
      (5, 'armor_001', '가죽 갑옷', 'ARMOR', 'COMMON', '가벼운 가죽 갑옷입니다.', 150, 75, NOW(), NOW()),
      (6, 'armor_002', '철 갑옷', 'ARMOR', 'UNCOMMON', '방어력이 좋은 철 갑옷입니다.', 400, 200, NOW(), NOW()),
      (7, 'potion_hp_001', 'HP 포션', 'CONSUMABLE', 'COMMON', 'HP를 50 회복합니다.', 30, 10, NOW(), NOW()),
      (8, 'potion_mp_001', 'MP 포션', 'CONSUMABLE', 'COMMON', 'MP를 30 회복합니다.', 30, 10, NOW(), NOW()),
      (9, 'gem_green_001', '초록 보석', 'ETC', 'RARE', '특별한 기운이 담긴 보석입니다.', 500, 250, NOW(), NOW()),
      (10, 'ticket_miracle_001', '미라클 티켓', 'ETC', 'EPIC', '미라클 타임 이벤트를 기념하는 티켓입니다.', 1000, 500, NOW(), NOW());


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
      (1, 'npc_shop_001', '상점 NPC', '아이템을 판매하는 NPC입니다.', 'TOWN_CENTER', TRUE, NOW(), NOW()),
      (2, 'npc_enhance_001', '강화 NPC', '아이템 강화를 진행하는 NPC입니다.', 'TOWN_FORGE', TRUE, NOW(), NOW()),
      (3, 'npc_random_reward_001', '확률형 아이템 NPC', '확률형 아이템 기능용 NPC입니다. 현재 사용하지 않습니다.', 'TOWN_EVENT', FALSE, NOW(), NOW());

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
      (1, 1, 1, 100, 'GOLD', NULL, 1, TRUE, NULL, NULL, NOW(), NOW()),
      (2, 1, 3, 200, 'GOLD', NULL, 2, TRUE, NULL, NULL, NOW(), NOW()),
      (3, 1, 5, 150, 'GOLD', NULL, 3, TRUE, NULL, NULL, NOW(), NOW()),
      (4, 1, 7, 30, 'GOLD', NULL, 4, TRUE, NULL, NULL, NOW(), NOW()),
      (5, 1, 8, 30, 'GOLD', NULL, 5, TRUE, NULL, NULL, NOW(), NOW());

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
      (1, 0, 1, 70.00, 30.00, 0.00, 1, TRUE, NOW(), NOW()),
      (2, 1, 2, 65.00, 35.00, 0.00, 2, TRUE, NOW(), NOW()),
      (3, 2, 3, 60.00, 40.00, 0.00, 3, TRUE, NOW(), NOW()),
      (4, 3, 4, 55.00, 40.00, 5.00, 4, TRUE, NOW(), NOW()),
      (5, 4, 5, 50.00, 40.00, 10.00, 5, TRUE, NOW(), NOW()),
      (6, 5, 6, 45.00, 40.00, 15.00, 8, TRUE, NOW(), NOW()),
      (7, 6, 7, 40.00, 40.00, 20.00, 10, TRUE, NOW(), NOW()),
      (8, 7, 8, 35.00, 40.00, 25.00, 12, TRUE, NOW(), NOW()),
      (9, 8, 9, 30.00, 40.00, 30.00, 15, TRUE, NOW(), NOW()),
      (10, 9, 10, 25.00, 40.00, 35.00, 20, TRUE, NOW(), NOW()),
      (11, 10, 11, 20.00, 40.00, 40.00, 25, TRUE, NOW(), NOW()),
      (12, 11, 12, 18.00, 42.00, 40.00, 30, TRUE, NOW(), NOW()),
      (13, 12, 13, 16.00, 44.00, 40.00, 35, TRUE, NOW(), NOW()),
      (14, 13, 14, 14.00, 46.00, 40.00, 40, TRUE, NOW(), NOW()),
      (15, 14, 15, 12.00, 48.00, 40.00, 45, TRUE, NOW(), NOW()),
      (16, 15, 16, 10.00, 50.00, 40.00, 50, TRUE, NOW(), NOW()),
      (17, 16, 17, 8.00, 52.00, 40.00, 60, TRUE, NOW(), NOW()),
      (18, 17, 18, 6.00, 54.00, 40.00, 70, TRUE, NOW(), NOW()),
      (19, 18, 19, 5.00, 55.00, 40.00, 80, TRUE, NOW(), NOW()),
      (20, 19, 20, 5.00, 55.00, 40.00, 100, TRUE, NOW(), NOW());

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