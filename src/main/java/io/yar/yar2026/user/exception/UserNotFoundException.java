package io.yar.yar2026.user.exception;

import io.yar.yar2026.common.constants.ErrorCode;
import io.yar.yar2026.common.exception.BusinessException;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
