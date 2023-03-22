package org.nop.icbc.appointment.checker.util;

import lombok.experimental.UtilityClass;
import org.apache.tomcat.util.codec.binary.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@UtilityClass
public class SecretUtils {

    public static String decodeB64(String value) {
        return new String(Base64.decodeBase64(value), UTF_8);
    }
}
