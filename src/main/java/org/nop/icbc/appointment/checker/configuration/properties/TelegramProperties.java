package org.nop.icbc.appointment.checker.configuration.properties;

import lombok.Value;
import org.nop.icbc.appointment.checker.util.SecretUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Value
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {
    String baseUri;
    String token;
    String chatId;

    public TelegramProperties(String baseUri, String token, String chatId) {
        this.baseUri = baseUri;
        this.token = SecretUtils.decodeB64(token);
        this.chatId = chatId;
    }

    public String getSendMessageUrl() {
        return baseUri + "/bot" + token + "/sendMessage";
    }
}
