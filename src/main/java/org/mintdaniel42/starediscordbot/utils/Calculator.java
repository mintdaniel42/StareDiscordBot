package org.mintdaniel42.starediscordbot.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;

@UtilityClass
public class Calculator {
	@Contract(pure = true, value = "_, _ -> param1")
	public double calculateLuck(final double quota, final double winrate) {
		return quota - winrate;
	}
}
