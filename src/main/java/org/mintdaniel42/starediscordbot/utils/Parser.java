package org.mintdaniel42.starediscordbot.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
@Deprecated(forRemoval = true)
public class Parser {
    public long parseLong(@NonNull String toLong) {
        Matcher numberMatcher = Pattern.compile("\\b(\\d+(\\.\\d+))").matcher(toLong);
        Matcher multiplierMatcher = Pattern.compile("[KkMmBbTt]").matcher(toLong);
        if (!numberMatcher.find()) return 0;
        long multiplier;
        if (!multiplierMatcher.find()) multiplier = 0;
        else {
            multiplier = switch (multiplierMatcher.group().toLowerCase()) {
                case "k" -> 1_000;
                case "m" -> 1_000_000;
                case "b" -> 1_000_000_000;
                case "t" -> 1_000_000_000_000L;
                default -> 0;
            };
        }
        String raw = numberMatcher.group();
        return Math.round(Double.parseDouble(raw) * multiplier);
    }

    public boolean isValidInt(@NonNull String toCheck) {
        return Pattern.compile("\\b(\\d+(\\.\\d+)?([KkMmBbTt]?|\\.))\\b")
                .matcher(toCheck)
                .find();
    }
}
