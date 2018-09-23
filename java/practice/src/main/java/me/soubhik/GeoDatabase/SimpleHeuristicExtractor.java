package me.soubhik.GeoDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by soubhik on 01-09-2016.
 */
public class SimpleHeuristicExtractor implements AddressFieldExtractor {
    private static String PIN = "pin";
    private static final String PIN_PATTERN1 = "pin[\\s]+";
    private static final String PIN_PATTERN2 = "pin[\\s]*[:-][\\s]*";
    private static final String CODE_PATTERN = "[1-9][\\d]{5}";
    private static final Pattern PINCODE_PATTERN1 = Pattern.compile(PIN_PATTERN1 + CODE_PATTERN);
    private static final Pattern PINCODE_PATTERN2 = Pattern.compile(PIN_PATTERN2 + CODE_PATTERN);
    private static final int CODE_LENGTH = 6;

    @Override
    public Map<String, String> extract(String addressLine) {
        Map<String, String> results = new HashMap<>();

        String pin = extractPin(addressLine);
        if (pin != null) {
            results.put(PIN, pin);
        }

        return results;
    }

    private String extractPin(String addressLine) {
        addressLine = addressLine.toLowerCase();
        Matcher matcher1 = PINCODE_PATTERN1.matcher(addressLine);
        String pin = extractPinFromLastMatch(addressLine, matcher1);
        if (pin != null) {
            return pin;
        }

        Matcher matcher2 = PINCODE_PATTERN2.matcher(addressLine);
        pin = extractPinFromLastMatch(addressLine, matcher2);

        return pin;
    }

    private String extractPinFromLastMatch(String addressLine, Matcher matcher) {
        int end = -1;
        while (matcher.find()) {
            end = matcher.end();
        }

        if (end == -1) {
            return null;
        }

        int start = end - CODE_LENGTH;
        if (start >= 0) {
            return addressLine.substring(start, end);
        }

        return null;
    }
}
