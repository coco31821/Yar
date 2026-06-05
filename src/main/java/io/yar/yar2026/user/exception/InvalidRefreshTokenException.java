package io.yar.yar2026.user.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {

        super("유효하지 않은 Refresh Token입니다.")
        ;
    }
}
