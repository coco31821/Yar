package io.yar.yar2026.item.exception;

import io.yar.yar2026.common.constants.ErrorCode;
import io.yar.yar2026.common.exception.BusinessException;

public class ItemNotFoundException extends BusinessException {
    public ItemNotFoundException() {super(ErrorCode.ITEM_NOT_FOUND);}
}
