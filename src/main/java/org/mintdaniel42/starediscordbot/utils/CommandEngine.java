package org.mintdaniel42.starediscordbot.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CommandEngine {
	public @NonNull List<CommandData> generateCommands(@NonNull final String filePath) {
		final var commands = new ArrayList<CommandData>();
		final var url = CommandEngine.class.getClassLoader().getResource(filePath);
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
