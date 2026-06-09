package io.yar.yar2026.enhancement.exception;

import io.yar.yar2026.common.constants.ErrorCode;
import io.yar.yar2026.common.exception.BusinessException;

public class ItemNotEnhanceableException extends BusinessException {

    public ItemNotEnhanceableException() {
        super(ErrorCode.ITEM_NOT_ENHANCEABLE);
    }
}

