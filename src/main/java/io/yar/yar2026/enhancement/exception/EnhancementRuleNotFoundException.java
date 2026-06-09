package io.yar.yar2026.enhancement.exception;

import io.yar.yar2026.common.constants.ErrorCode;
import io.yar.yar2026.common.exception.BusinessException;

public class EnhancementRuleNotFoundException extends BusinessException {

    public EnhancementRuleNotFoundException() {
        super(ErrorCode.ENHANCEMENT_RULE_NOT_FOUND);
    }
}

