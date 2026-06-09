package io.yar.yar2026.common.config.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "custom.jwt")
public class JwtProperties {

    private final Validations validations;
    private final Secrets secrets;

    @Getter
    @RequiredArgsConstructor
    public static class Validations {
        private final Long access;
        private final Long refresh;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Secrets {
        private final String appKey;
        private final String originKey;
    }
}
