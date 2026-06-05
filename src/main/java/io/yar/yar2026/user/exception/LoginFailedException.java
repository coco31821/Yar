package io.yar.yar2026.user.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;

public class LoginFailedException extends RuntimeException{
    public LoginFailedException(){
        super("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
}
