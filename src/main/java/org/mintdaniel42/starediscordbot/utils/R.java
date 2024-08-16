package org.mintdaniel42.starediscordbot.utils;

import io.avaje.jsonb.JsonType;
import io.avaje.jsonb.Jsonb;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.entity.TutorialEntity;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * A utility class for easier resource access
 */
@UtilityClass
public class R {
	/**
	 * This utility class holds different emojis
	 */
	@UtilityClass
	public class Emojis {
		public final Emoji arrowLeft = Emoji.fromUnicode("⬅");
		public final Emoji arrowRight = Emoji.fromUnicode("➡");
		public final Emoji information = Emoji.fromUnicode("ℹ");
		public final Emoji group = Emoji.fromUnicode("\uD83D\uDC65");
		public final Emoji approve = Emoji.fromUnicode("✅");
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

	/**
	 * This utility class manages and holds all loaded tutorial files
	 */
	@UtilityClass
	public class Tutorials {
		private final String prefix = "tutorials/de/"; // TODO: replace this
		private final JsonType<TutorialEntity> tutorialType = Jsonb.builder()
				.build()
				.type(TutorialEntity.class);
		@Getter(lazy = true) @Accessors(fluent = true) @NonNull private final TutorialEntity[] list = loadTutorials();

		/**
		 * Get all tutorial ids in the {@code tutorials/de} directory
		 * @return array of all tutorial ids
		 */
		private @NonNull TutorialEntity[] loadTutorials() {
			try {
				return IOUtils.readLines(Objects.requireNonNull(Tutorials.class.getClassLoader().getResourceAsStream(prefix)), StandardCharsets.UTF_8)
						.stream()
						.filter(s -> s.charAt(0) != '.')
						.map(s -> get(s.substring(0, s.length() - 5)))
						.filter(Objects::nonNull)
						.sorted()
						.toArray(TutorialEntity[]::new);
			} catch (UncheckedIOException | IllegalArgumentException | NullPointerException _) {
				return new TutorialEntity[0];
			}
		}

		/**
		 * Load a tutorial from the bots' resource files
		 *
		 * @param id the tutorial id
		 * @return the model of the tutorial
		 */
		public @Nullable TutorialEntity get(@NonNull final String id) {
			try (var inputStream = R.Tutorials.class.getClassLoader().getResourceAsStream(prefix + id + ".json")) {
				return tutorialType.fromJson(inputStream)
						.toBuilder()
						.id(id)
						.build();
			} catch (IOException | IllegalArgumentException _) {
				return null;
			}
		}
	}
}