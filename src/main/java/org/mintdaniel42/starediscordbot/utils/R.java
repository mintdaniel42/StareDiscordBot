package org.mintdaniel42.starediscordbot.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

@UtilityClass
public class R {
	public @NonNull String string(@NonNull @PropertyKey(resourceBundle = "strings") final String string) {
		return ResourceBundle.getBundle("strings", Options.getLocale()).getString(string);
	}
}
