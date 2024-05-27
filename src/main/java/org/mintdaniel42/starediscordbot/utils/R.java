package org.mintdaniel42.starediscordbot.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.PropertyKey;
import org.mintdaniel42.starediscordbot.build.BuildConfig;

import java.util.ResourceBundle;

/**
 * A utility class for easier resource access
 */
@UtilityClass
public class R {
	@Deprecated
	public @NonNull String string(@NonNull @PropertyKey(resourceBundle = "ui") final String string, Object... args) {
		return Strings.ui(string, args);
	}

	@Deprecated
	public @NonNull String logging(@NonNull @PropertyKey(resourceBundle = "log") final String string, Object... args) {
		return Strings.log(string, args);
	}

	/**
	 * This utility class contains two methods:<br>
	 * {@code Strings.ui(stringKey, (optional) arguments...)}<br>
	 * {@code Strings.log(stringKey, (optional) arguments...)}<br>
	 * Both of them do the same thing but for two different resource bundles.
	 * The first one handles all strings the user can see in messages and commands
	 * while the second one contains all strings that are used for logging.
	 */
	@UtilityClass
	public class Strings {
		/**
		 * Get strings from the resource bundle {@code ui}
		 *
		 * @param string the string key in the resource bundle
		 * @param args   if formatting qualifiers are present they have to be specified here
		 * @return the string from the resourcebundle if it exists, else the string key
		 */
		public @NonNull String ui(@NonNull @PropertyKey(resourceBundle = "ui") final String string, Object... args) {
			ResourceBundle resourceBundle = ResourceBundle.getBundle("ui", BuildConfig.locale);
			return resourceBundle.containsKey(string) ? resourceBundle.getString(string).formatted(args) : string;
		}

		/**
		 * Get strings from the resource bundle {@code log}
		 *
		 * @param string the string key in the resource bundle
		 * @param args   if formatting qualifiers are present they have to be specified here
		 * @return the string from the resourcebundle if it exists, else the string key
		 */
		public @NonNull String log(@NonNull @PropertyKey(resourceBundle = "log") final String string, Object... args) {
			ResourceBundle resourceBundle = ResourceBundle.getBundle("log", BuildConfig.locale);
			return resourceBundle.containsKey(string) ? resourceBundle.getString(string).formatted(args) : string;
		}
	}
}
