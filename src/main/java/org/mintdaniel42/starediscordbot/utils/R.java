package org.mintdaniel42.starediscordbot.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.PropertyKey;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mintdaniel42.starediscordbot.build.BuildConfig;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * A utility class for easier resource access
 */
@UtilityClass
public class R {
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

	/**
	 * This utility class is used to load the commands
	 */
	@UtilityClass
	public class Commands {
		/**
		 * Load the commands from the specified json file
		 *
		 * @param filePath path of the json file
		 * @return list of commands
		 */
		public @NonNull List<CommandData> load(@NonNull final String filePath) {
			final var commands = new ArrayList<CommandData>();
			final var url = R.Commands.class.getClassLoader().getResource(filePath);
			try {
				final var content = new JSONObject(Files.readString(Path.of(url.toURI())));

				for (final String commandKey : content.keySet()) {
					if (content.get(commandKey) instanceof final JSONObject commandJson) {
						prepareJson(commandJson).put("name", commandKey);

						//#if dev==false
						if (!commandJson.has("dev") || !commandJson.getBoolean("dev")) {
							//#endif
							commands.add(CommandData.fromData(DataObject.fromJson(prepareJson(commandJson)
									.put("name", commandKey)
									.toString())));
							//#if dev==false
						}
						//#endif
					}
				}
			} catch (IOException | URISyntaxException _) {
			}
			return commands;
		}

		@Contract(pure = true, value = "_ -> new")
		private @NonNull JSONObject prepareJson(@NonNull final JSONObject json) {
			// TODO: translate choices
			final var newJsonObject = new JSONObject();
			for (final var key : json.keySet()) {
				if (json.get(key) instanceof final JSONObject jsonObject) {
					newJsonObject.put(key, prepareJson(jsonObject));
				} else if (json.get(key) instanceof final JSONArray jsonArray) {
					for (int i = 0; i < jsonArray.length(); i++) {
						if (jsonArray.get(i) instanceof JSONObject jsonObject) {
							jsonArray.put(i, prepareJson(jsonObject));
						}
					}
					newJsonObject.put(key, jsonArray);
				} else {
					if (!key.equals("description")) {
						newJsonObject.put(key, json.get(key));
					} else newJsonObject.put(key, R.Strings.ui(json.getString(key)));
				}
			}
			return newJsonObject;
		}
	}
}
