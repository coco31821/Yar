package io.yar.yar2026.friend.exception;

import io.yar.yar2026.common.constants.ErrorCode;
import io.yar.yar2026.common.exception.BusinessException;

public class FriendRequestNotFoundException extends BusinessException {

    public FriendRequestNotFoundException() {
        super(ErrorCode.FRIEND_REQUEST_NOT_FOUND);
    }
}
