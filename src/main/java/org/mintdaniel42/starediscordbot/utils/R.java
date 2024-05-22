package org.mintdaniel42.starediscordbot.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.PropertyKey;
import org.mintdaniel42.starediscordbot.build.BuildConfig;

import java.util.ResourceBundle;

@UtilityClass
public class R {
	public @NonNull String string(@NonNull @PropertyKey(resourceBundle = "strings") final String string, Object... args) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("strings", BuildConfig.locale);
		return resourceBundle.containsKey(string) ? resourceBundle.getString(string).formatted(args) : string;
	}

	public @NonNull String logging(@NonNull @PropertyKey(resourceBundle = "logging") final String string, Object... args) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("logging", BuildConfig.locale);
		return resourceBundle.containsKey(string) ? resourceBundle.getString(string).formatted(args) : string;
	}
}
