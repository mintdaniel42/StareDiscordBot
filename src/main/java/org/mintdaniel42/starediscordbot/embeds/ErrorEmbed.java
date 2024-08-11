package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Arrays;

@UtilityClass
public class ErrorEmbed {
	public @NonNull MessageEmbed of(@NonNull final SlashCommandInteraction interaction, @NonNull final Exception exception) {
		final var commandBuilder = new StringBuilder();
		interaction.getOptions()
				.forEach(optionMapping -> commandBuilder.append(optionMapping.getName())
						.append(":")
						.append(optionMapping.getAsString()));

		return build(exception, interaction.getFullCommandName() + " " + commandBuilder);
	}

	public @NonNull MessageEmbed of(@NonNull final String componentId, @NonNull final Exception exception) {
		return build(exception, componentId);
	}

	@Contract(pure = true)
	private @NonNull MessageEmbed build(@NonNull final Exception exception, @NonNull final String name) {
		final var stackTraceBuilder = new StringBuilder();
		Arrays.stream(exception.getStackTrace())
				.limit(5)
				.forEach(stackTraceElement -> stackTraceBuilder.append(String.format("%s:%s\n", stackTraceElement.getClassName(), stackTraceElement.getLineNumber())));

		return new EmbedBuilder()
				.setTitle(R.Strings.ui("an_impossible_error_occurred"))
				.setColor(Options.getColorNormal())
				.addField(R.Strings.ui("the_error"), exception.toString(), false)
				.addField(R.Strings.ui("location_of_the_error"), stackTraceBuilder.toString(), false)
				.addField(R.Strings.ui("the_button_you_pressed"), name, false)
				.build();
	}
}
