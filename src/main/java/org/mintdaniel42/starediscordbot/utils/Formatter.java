package org.mintdaniel42.starediscordbot.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;

import java.text.DecimalFormat;

@UtilityClass
public class Formatter {
	@Contract(pure = true, value = "_ -> new")
	@NonNull
	public String formatNumber(double value) {
		final var stringBuilder = new StringBuilder("##.0");

		if (value >= 10_000_000_000L) {
			value /= 1_000_000_000L;
			stringBuilder.append('B');
		} else if (value >= 10_000_000L) {
			value /= 1_000_000;
			stringBuilder.append('M');
		} else if (value >= 10_000) {
			value /= 1_000;
			stringBuilder.append('K');
		}

		return new DecimalFormat(stringBuilder.toString()).format(value);
	}
}
