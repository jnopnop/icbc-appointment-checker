package org.nop.icbc.appointment.checker.configuration;

import org.nop.icbc.appointment.checker.IcbcAppointmentChecker;
import org.nop.icbc.appointment.checker.configuration.properties.DriverProperties;
import org.nop.icbc.appointment.checker.configuration.properties.IcbcProperties;
import org.nop.icbc.appointment.checker.configuration.properties.TelegramProperties;
import org.nop.icbc.appointment.checker.support.ExternalClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableRetry
@EnableScheduling
@EnableConfigurationProperties({
        DriverProperties.class,
        IcbcProperties.class,
        TelegramProperties.class
})
public class CommonConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ExternalClient externalClient(RestTemplate restTemplate) {
        return new ExternalClient(restTemplate);
    }

    @Bean
    public IcbcAppointmentChecker appointmentChecker(ExternalClient externalClient,
                                                     DriverProperties driverProperties,
                                                     IcbcProperties icbcProperties,
                                                     TelegramProperties telegramProperties) {
        return new IcbcAppointmentChecker(
                externalClient,
                driverProperties,
                icbcProperties,
                telegramProperties);
    }
}
