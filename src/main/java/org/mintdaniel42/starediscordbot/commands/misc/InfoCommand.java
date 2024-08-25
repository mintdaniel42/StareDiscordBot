package org.mintdaniel42.starediscordbot.commands.misc;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.buttons.list.InfoButtons;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.repository.*;
import org.mintdaniel42.starediscordbot.embeds.InfoEmbed;

@RequiredArgsConstructor
@Singleton
public final class InfoCommand implements CommandAdapter {
	@NonNull final MetaDataRepository metaDataRepository;
	@NonNull final HNSUserRepository hnsUserRepository;
	@NonNull final PGUserRepository pgUserRepository;
	@NonNull final UsernameRepository usernameRepository;
	@NonNull final GroupRepository groupRepository;
	@NonNull final SpotRepository spotRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		return interactionHook.editOriginalEmbeds(InfoEmbed.of(metaDataRepository.selectFirst().version(),
						usernameRepository.count(),
						hnsUserRepository.count(),
						pgUserRepository.count(),
						groupRepository.count(),
						spotRepository.count()))
				.setComponents(InfoButtons.create());
	}
}
