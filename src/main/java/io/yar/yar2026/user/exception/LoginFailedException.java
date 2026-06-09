package io.yar.yar2026.user.exception;

import io.yar.yar2026.common.constants.ErrorCode;
import io.yar.yar2026.common.exception.BusinessException;

public class LoginFailedException extends BusinessException {

    public LoginFailedException() {
        super(ErrorCode.LOGIN_FAILED);
    }
}
