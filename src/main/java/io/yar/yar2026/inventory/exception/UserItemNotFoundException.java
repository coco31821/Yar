package io.yar.yar2026.inventory.exception;

import io.yar.yar2026.common.constants.ErrorCode;
import io.yar.yar2026.common.exception.BusinessException;

public class UserItemNotFoundException extends BusinessException {
    public UserItemNotFoundException() {
        super(ErrorCode.USER_ITEM_NOT_FOUND);
    }
}