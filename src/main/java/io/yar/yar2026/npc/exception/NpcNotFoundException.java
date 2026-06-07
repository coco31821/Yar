package io.yar.yar2026.npc.exception;

import io.yar.yar2026.common.constants.ErrorCode;
import io.yar.yar2026.common.exception.BusinessException;

public class NpcNotFoundException extends BusinessException {
    public NpcNotFoundException() {
        super(ErrorCode.NPC_NOT_FOUND);
    }
}