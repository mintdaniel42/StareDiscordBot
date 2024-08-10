package org.mintdaniel42.starediscordbot.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.TutorialModel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * A utility class for easier resource access
 */
@UtilityClass
public class R {
	private final ObjectMapper objectMapper = new ObjectMapper();

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

	@UtilityClass
	public class Tutorials {
		// TODO: replace this
		private final String prefix = "tutorials/de/";

		/**
		 * Get all tutorial ids in the {@code tutorials/de} directory
		 *
		 * @return array of all tutorial ids
		 */
		public @NonNull String[] list() {
			try {
				return IOUtils.readLines(R.Tutorials.class.getClassLoader().getResourceAsStream(prefix), StandardCharsets.UTF_8)
						.stream()
						.map(s -> s.substring(0, s.length() - 5))
						.toArray(String[]::new);
			} catch (IOException _) {
				return new String[0];
			}
		}

		/**
		 * Load a tutorial from the bots' resource files
		 *
		 * @param id the tutorial id
		 * @return the model of the tutorial
		 */
		public @Nullable TutorialModel get(@NonNull final String id) {
			try (var inputStream = R.Tutorials.class.getClassLoader().getResourceAsStream(prefix + id + ".json")) {
				return objectMapper.readValue(inputStream, TutorialModel.class)
						.toBuilder()
						.id(id)
						.build();
			} catch (IOException _) {
				return null;
			}
		}
	}
}