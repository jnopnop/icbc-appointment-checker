package org.nop.icbc.appointment.checker.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

@ConfigurationProperties(prefix = "icbc")
public record IcbcProperties(
        String authUrl,
        String availabilityUrl,
        String userAgent,
        String examType,
        Map<Integer, String> locations,
        String preferredDaysOfWeek,
        String preferredPartsOfDay,
        boolean ignoreReserveTime,
        Duration appointmentMinDelayFromNow,
        Duration appointmentMaxDelayFromNow
) {
}
