package io.xhub.smwall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.email")
public class EmailProperties {
    private String username;
    private String password;
    private Smtp smtp;

    @Data
    public static class Smtp {
        private boolean auth;
        private String host;
        private int port;
        private boolean starttlsEnable;
        private String sslProtocols;

    }
}
