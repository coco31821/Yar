package io.yar.yar2026.wallet.exception;

import io.yar.yar2026.common.constants.ErrorCode;
import io.yar.yar2026.common.exception.BusinessException;

public class NotEnoughGoldException extends BusinessException {
    public NotEnoughGoldException() {
        super(ErrorCode.NOT_ENOUGH_GOLD);
    }
}
