package io.yar.yar2026.friend.exception;

import io.yar.yar2026.common.constants.ErrorCode;
import io.yar.yar2026.common.exception.BusinessException;

public class InvalidFriendRequestException extends BusinessException {

    public InvalidFriendRequestException(String message) {
        super(ErrorCode.VALIDATION_FAILED, message);
    }
}
