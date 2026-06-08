package io.yar.yar2026.enhancement.exception;

import io.yar.yar2026.common.constants.ErrorCode;
import io.yar.yar2026.common.exception.BusinessException;

public class EnhancementMaxGradeException extends BusinessException {

    public EnhancementMaxGradeException() {
        super(ErrorCode.ENHANCEMENT_MAX_GRADE);
    }
}

