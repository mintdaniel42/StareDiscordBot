package org.mintdaniel42.starediscordbot.commands.misc;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.buttons.list.InfoButtons;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.compose.exception.ComposeException;
import org.mintdaniel42.starediscordbot.data.repository.*;
import org.mintdaniel42.starediscordbot.embeds.InfoEmbed;

@RequiredArgsConstructor
@Singleton
public final class InfoCommand extends BaseComposeCommand {
	@NonNull final MetaDataRepository metaDataRepository;
	@NonNull final HNSUserRepository hnsUserRepository;
	@NonNull final PGUserRepository pgUserRepository;
	@NonNull final ProfileRepository profileRepository;
	@NonNull final GroupRepository groupRepository;
	@NonNull final SpotRepository spotRepository;
	@NonNull final BotConfig config;

	@Override
	public @NonNull MessageEditData compose(@NonNull final CommandContext context) throws ComposeException {
		return response()
				.setEmbeds(new InfoEmbed(config,
						metaDataRepository.selectFirst().version(),
						profileRepository.count(),
						hnsUserRepository.count(),
						pgUserRepository.count(),
						groupRepository.count(),
						spotRepository.count()))
				.setComponents(InfoButtons.create())
				.build();
	}

	@Override
	public @NonNull String getCommandId() {
		return "info";
	}
}
