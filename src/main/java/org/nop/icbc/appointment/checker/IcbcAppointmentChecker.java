package org.nop.icbc.appointment.checker;

import lombok.RequiredArgsConstructor;
import org.nop.icbc.appointment.checker.configuration.properties.DriverProperties;
import org.nop.icbc.appointment.checker.configuration.properties.IcbcProperties;
import org.nop.icbc.appointment.checker.configuration.properties.TelegramProperties;
import org.nop.icbc.appointment.checker.support.ExternalClient;
import org.nop.icbc.appointment.checker.util.DateTimeUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.nop.icbc.appointment.checker.util.DateTimeUtils.toLocalDateTime;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.USER_AGENT;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class IcbcAppointmentChecker {
    private final ExternalClient client;
    private final DriverProperties driverProperties;
    private final IcbcProperties icbcProperties;
    private final TelegramProperties telegramProperties;

    public void findAndNotify() {
        String auth = authenticate();

        LocalDateTime minDate = DateTimeUtils.now().plus(icbcProperties.appointmentMinDelayFromNow());
        LocalDateTime maxDate = DateTimeUtils.now().plus(icbcProperties.appointmentMaxDelayFromNow());

        List<Appointment> found = icbcProperties.locations().keySet().stream()
                .map(location -> findAppointmentsAt(auth, location))
                .flatMap(Collection::stream)
                .map(this::toAppointment)
                .filter(apt -> apt.dateTime().isAfter(minDate) && apt.dateTime().isBefore(maxDate))
                .collect(toList());

        String message = toMessage(found);
        sendMessage(message);
    }

    private String authenticate() {
        return client.request(icbcProperties.authUrl(), PUT,
                        Map.of(
                                "drvrLastName", driverProperties.getLastName(),
                                "licenceNumber", driverProperties.getLicenseNumber(),
                                "keyword", driverProperties.getKeyword()
                        ),
                        Map.of(
                                CONTENT_TYPE, APPLICATION_JSON_VALUE,
                                ACCEPT, APPLICATION_JSON_VALUE,
                                USER_AGENT, icbcProperties.userAgent()
                        ))
                .getHeaders().getFirst(AUTHORIZATION);
    }

    private List<Map<String, Object>> findAppointmentsAt(String auth, int location) {
        ResponseEntity<List<Map<String, Object>>> response = client.request(icbcProperties.availabilityUrl(), POST,
                Map.of(
                        "aPosID", location,
                        "examDate", DateTimeUtils.today(),
                        "examType", icbcProperties.examType(),
                        "ignoreReserveTime", icbcProperties.ignoreReserveTime(),
                        "lastName", driverProperties.getLastName(),
                        "licenseNumber", driverProperties.getLicenseNumber(),
                        "prfDaysOfWeek", icbcProperties.preferredDaysOfWeek(),
                        "prfPartsOfDay", icbcProperties.preferredPartsOfDay()
                ),
                Map.of(
                        AUTHORIZATION, auth,
                        CONTENT_TYPE, APPLICATION_JSON_VALUE,
                        ACCEPT, APPLICATION_JSON_VALUE,
                        USER_AGENT, icbcProperties.userAgent()
                ));

        return Optional.ofNullable(response.getBody())
                .orElseGet(Collections::emptyList);
    }

    private void sendMessage(String message) {
        client.request(telegramProperties.getSendMessageUrl(), HttpMethod.POST, Map.of(
                "chat_id", telegramProperties.getChatId(),
                "text", message
        ), Map.of(
                CONTENT_TYPE, APPLICATION_JSON_VALUE,
                ACCEPT, APPLICATION_JSON_VALUE
        ));
    }

    @SuppressWarnings("unchecked")
    private Appointment toAppointment(Map<String, Object> entry) {
        Map<String, Object> dateContainer = (Map<String, Object>) entry.get("appointmentDt");
        String date = (String) dateContainer.get("date");
        String time = (String) entry.get("startTm");
        Integer location = (Integer) entry.get("posId");

        return new Appointment(toLocalDateTime(date, time), location);
    }

    private String toMessage(List<Appointment> appointments) {
        return appointments
                .stream()
                .collect(groupingBy(
                        apt -> apt.dateTime().toLocalDate(),
                        TreeMap::new,
                        mapping(this::toLocation, toSet())))
                .entrySet()
                .stream()
                .map(entry -> String.format("%s at %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));
    }

    private String toLocation(Appointment apt) {
        return icbcProperties.locations().get(apt.location());
    }

    private record Appointment(
            LocalDateTime dateTime,
            int location
    ) {
    }
}
