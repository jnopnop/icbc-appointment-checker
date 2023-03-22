package org.nop.icbc.appointment.checker.configuration.properties;

import lombok.Value;
import org.nop.icbc.appointment.checker.util.SecretUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Value
@ConfigurationProperties(prefix = "driver")
public class DriverProperties {
    String lastName;
    String keyword;
    String licenseNumber;

    public DriverProperties(String lastName, String keyword, String licenseNumber) {
        this.lastName = SecretUtils.decodeB64(lastName);
        this.keyword = SecretUtils.decodeB64(keyword);
        this.licenseNumber = SecretUtils.decodeB64(licenseNumber);
    }
}
