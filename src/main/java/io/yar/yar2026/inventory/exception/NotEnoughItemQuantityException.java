package io.yar.yar2026.inventory.exception;

import io.yar.yar2026.common.constants.ErrorCode;
import io.yar.yar2026.common.exception.BusinessException;

public class NotEnoughItemQuantityException extends BusinessException {
    public NotEnoughItemQuantityException() {
        super(ErrorCode.NOT_ENOUGH_ITEM_QUANTITY);
    }
}