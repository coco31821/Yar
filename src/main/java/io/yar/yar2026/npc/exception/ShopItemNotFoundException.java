package io.yar.yar2026.npc.exception;

import io.yar.yar2026.common.constants.ErrorCode;
import io.yar.yar2026.common.exception.BusinessException;

public class ShopItemNotFoundException extends BusinessException {
    public ShopItemNotFoundException() {
        super(ErrorCode.SHOP_ITEM_NOT_FOUND);
    }
}
