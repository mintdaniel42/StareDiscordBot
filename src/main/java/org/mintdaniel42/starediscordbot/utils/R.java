package org.mintdaniel42.starediscordbot.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

@UtilityClass
public class R {
	public @NonNull String string(@NonNull @PropertyKey(resourceBundle = "strings") final String string) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("strings", Options.getLocale());
		return resourceBundle.containsKey(string) ? resourceBundle.getString(string) : string;
	}

	public @NonNull String logging(@NonNull @PropertyKey(resourceBundle = "logging") final String string) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("logging", Options.getLocale());
		return resourceBundle.containsKey(string) ? resourceBundle.getString(string) : string;
	}
}
