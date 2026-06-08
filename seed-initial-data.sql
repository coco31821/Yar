-- MySQL seed data for local Unity backend testing.
-- Login test account:
--   email: gamer@test.com
--   password: password

use yar;

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM npc_sale_items;
DELETE FROM npcs;

INSERT INTO items (
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
      (1, 'sword_bronze_001', '청동 검', 'WEAPON', 'COMMON', '처음 들기 좋은 가벼운 검입니다.', 120, 60, NOW(), NOW()),
      (2, 'bow_hunter_001', '사냥꾼 활', 'WEAPON', 'UNCOMMON', '먼 거리의 적을 노릴 수 있는 활입니다.', 260, 130, NOW(), NOW()),
      (3, 'armor_leather_001', '가죽 갑옷', 'ARMOR', 'COMMON', '움직이기 편한 기본 갑옷입니다.', 180, 90, NOW(), NOW()),
      (4, 'potion_hp_001', '체력 물약', 'CONSUMABLE', 'COMMON', '체력을 조금 회복합니다.', 50, 15, NOW(), NOW()),
      (5, 'stone_lucky_001', '행운의 돌', 'ETC', 'RARE', '묘하게 좋은 일이 생길 것 같은 돌입니다.', 500, 250, NOW(), NOW())
ON DUPLICATE KEY UPDATE
      r_id = VALUES(r_id),
      item_name = VALUES(item_name),
      item_type = VALUES(item_type),
      item_grade = VALUES(item_grade),
      description = VALUES(description),
      price = VALUES(price),
      sell_price = VALUES(sell_price),
      updated_at = NOW();


INSERT INTO npcs (
    npc_id,
    r_id,
    name,
    description,
    location_key,
    active,
    created_at,
    updated_at
) VALUES
      (1, 'npc_general_store_001', '마을 잡화상', '여행에 필요한 기본 물품을 판매합니다.', 'village_center', TRUE, NOW(), NOW()),
      (2, 'npc_weapon_shop_001', '무기 상인', '초보자용 무기를 판매합니다.', 'weapon_shop', TRUE, NOW(), NOW()),
      (3, 'npc_armor_shop_001', '방어구 상인', '튼튼한 방어구를 판매합니다.', 'armor_shop', TRUE, NOW(), NOW()),
      (4, 'npc_potion_shop_001', '연금술사', '회복 물약과 소모품을 판매합니다.', 'alchemy_shop', TRUE, NOW(), NOW()),
      (5, 'npc_rare_shop_001', '수상한 상인', '희귀한 물건을 조용히 거래합니다.', 'back_alley', TRUE, NOW(), NOW());

INSERT INTO npc_sale_items (
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
      (1, 1, 4, 45, 'GOLD', 30, 1, TRUE, NULL, NULL, NOW(), NOW()),
      (2, 2, 1, 120, 'GOLD', NULL, 1, TRUE, NULL, NULL, NOW(), NOW()),
      (3, 2, 2, 250, 'GOLD', NULL, 2, TRUE, NULL, NULL, NOW(), NOW()),
      (4, 3, 3, 170, 'GOLD', NULL, 1, TRUE, NULL, NULL, NOW(), NOW()),
      (5, 5, 5, 480, 'GOLD', 5, 1, TRUE, NULL, NULL, NOW(), NOW());

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
