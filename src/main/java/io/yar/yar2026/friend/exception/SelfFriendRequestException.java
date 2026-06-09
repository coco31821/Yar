package io.yar.yar2026.friend.exception;

import io.yar.yar2026.common.constants.ErrorCode;
import io.yar.yar2026.common.exception.BusinessException;

public class SelfFriendRequestException extends BusinessException {

    public SelfFriendRequestException() {
        super(ErrorCode.SELF_FRIEND_REQUEST);
    }
}
