package org.mintdaniel42.starediscordbot.commands.pg;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.buttons.list.PGListButtons;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.repository.PGUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;

@RequiredArgsConstructor
@Singleton
public final class PGListCommand extends BaseComposeCommand {
	@NonNull private final PGUserRepository pgUserRepository;
	@NonNull private final ProfileRepository profileRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		final var page = nullableIntegerOption(context, "page").orElse(1) - 1;
		final var pageCount = (int) Math.ceil((double) pgUserRepository.count() / BuildConfig.entriesPerPage);
		requireBounds(0, page, pageCount);
		var entries = pgUserRepository.selectAll(page * BuildConfig.entriesPerPage, BuildConfig.entriesPerPage);
		return response()
				.setEmbeds(ListEmbed.createPgList(profileRepository, entries, page, pageCount))
				.setComponents(PGListButtons.create(page, pageCount))
				.build();
	}

	@Override
	public @NonNull String getCommandId() {
		return "pg list";
	}
}
