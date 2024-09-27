package org.mintdaniel42.starediscordbot.compose.command;

import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.mintdaniel42.starediscordbot.compose.Context;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public final class CommandContext extends Context {
	@NonNull private final List<OptionMapping> options;

	public CommandContext(@NonNull final SlashCommandInteractionEvent event) {
		if (event.getMember() == null) throw new IllegalStateException();
		super(event.getJDA(), event.getMember());
		options = event.getOptions();
	}

	public @NonNull Optional<OptionMapping> getOption(@NonNull final String key) {
		return options.stream()
				.filter(Objects::nonNull)
				.filter(optionMapping -> optionMapping.getName().equals(key))
				.findFirst();
	}

	public int getOptionCount() {
		return options.size();
	}
}