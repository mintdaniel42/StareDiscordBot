package org.mintdaniel42.starediscordbot.commands.hns.maps;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.repository.MapRepository;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@Singleton
public final class MapsAddCommand implements CommandAdapter {
	@NonNull private final MapRepository mapRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("name") instanceof final OptionMapping nameMapping &&
				event.getOption("builder") instanceof final OptionMapping builderMapping &&
				event.getOption("release") instanceof final OptionMapping releaseMapping &&
				event.getOption("picture") instanceof final OptionMapping pictureMapping &&
				event.getOption("difficulty") instanceof final OptionMapping difficultyMapping) {
			return null;
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
