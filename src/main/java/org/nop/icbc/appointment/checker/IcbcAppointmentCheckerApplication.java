package org.nop.icbc.appointment.checker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;


@SpringBootApplication
public class IcbcAppointmentCheckerApplication implements CommandLineRunner {
    @Autowired
    private IcbcAppointmentChecker appointmentChecker;
    @Autowired
    private TaskScheduler taskScheduler;
    @Value("${app.recheck-interval-seconds:0}")
    private int recheckIntervalSeconds;

    public static void main(String[] args) {
        SpringApplication.run(IcbcAppointmentCheckerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (recheckIntervalSeconds <= 0) {
            appointmentChecker.findAndNotify();
            return;
        }

        taskScheduler.scheduleAtFixedRate(
                appointmentChecker::findAndNotify,
                Duration.ofSeconds(recheckIntervalSeconds));
    }
}
