package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.di.DI;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Arrays;
import java.util.List;

public class ErrorEmbed extends MessageEmbed {
	public ErrorEmbed(@NonNull final ButtonInteraction interaction, @NonNull final Exception exception) {
		super(
				null,
				R.Strings.ui("an_impossible_error_occurred"),
				null,
				EmbedType.RICH,
				null,
				DI.get(BotConfig.class).getColorNormal(),
				null,
				null,
				null,
				null,
				null,
				null,
				createFields(exception, interaction.getComponentId(), true)
		);
	}

	public ErrorEmbed(@NonNull final SlashCommandInteraction interaction, @NonNull final Exception exception) {
		final var nameBuilder = new StringBuilder(interaction.getFullCommandName() + " ");
		interaction.getOptions()
				.forEach(optionMapping -> nameBuilder.append(optionMapping.getName())
						.append(":")
						.append(optionMapping.getAsString()));
		super(
				null,
				R.Strings.ui("an_impossible_error_occurred"),
				null,
				EmbedType.RICH,
				null,
				DI.get(BotConfig.class).getColorNormal(),
				null,
				null,
				null,
				null,
				null,
				null,
				createFields(exception, nameBuilder.toString(), false)
		);
	}

	@Contract(pure = true)
	private static @NonNull List<Field> createFields(@NonNull final Exception exception, @NonNull final String name, final boolean isButton) {
		final var stackTraceBuilder = new StringBuilder();
		Arrays.stream(exception.getStackTrace())
				.limit(5)
				.forEach(stackTraceElement -> stackTraceBuilder.append(String.format("%s:%s\n", stackTraceElement.getClassName(), stackTraceElement.getLineNumber())));
		return List.of(
				new Field(R.Strings.ui("the_error"), exception.toString(), false),
				new Field(R.Strings.ui("location_of_the_error"), stackTraceBuilder.toString(), false),
				new Field(R.Strings.ui(isButton ? "the_button_that_was_pressed" : "the_command_that_was_executed"), name, false)
		);
	}
}
