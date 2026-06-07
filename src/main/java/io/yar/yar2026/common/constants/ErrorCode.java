package io.yar.yar2026.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "검증에 실패했습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    USER_EMAIL_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),

    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "아이템을 찾을 수 없습니다."),
    USER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "보유 아이템을 찾을 수 없습니다."),
    NOT_ENOUGH_GOLD(HttpStatus.BAD_REQUEST, "골드가 부족합니다."),
    ENHANCEMENT_MAX_GRADE(HttpStatus.BAD_REQUEST, "이미 최대 강화 단계입니다."),
    NPC_NOT_FOUND(HttpStatus.NOT_FOUND, "NPC를 찾을 수 없습니다."),
    SHOP_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "상점 아이템을 찾을 수 없습니다."),
    FRIEND_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다."),
    SELF_FRIEND_REQUEST(HttpStatus.BAD_REQUEST, "자기 자신에게 친구 요청을 보낼 수 없습니다."),
    FRIEND_REQUEST_DUPLICATED(HttpStatus.CONFLICT, "이미 친구 관계입니다. / 이미 보낸 친구 요청이 있습니다.");

    private final HttpStatus status;
    private final String message;
}
