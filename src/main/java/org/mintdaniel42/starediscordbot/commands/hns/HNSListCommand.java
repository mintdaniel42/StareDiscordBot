package org.mintdaniel42.starediscordbot.commands.hns;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.buttons.list.HNSListButtons;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.compose.exception.ComposeException;
import org.mintdaniel42.starediscordbot.data.exceptions.DatabaseException;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;

@RequiredArgsConstructor
@Singleton
public final class HNSListCommand extends BaseComposeCommand {
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final ProfileRepository profileRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws ComposeException, DatabaseException {
		final int page = nullableIntegerOption(context, "page").orElse(1) - 1;
		final var pageCount = (int) Math.ceil((double) hnsUserRepository.count() / BuildConfig.entriesPerPage);
		requireBounds(0, page, pageCount);
		final var entries = hnsUserRepository.selectAll(page * BuildConfig.entriesPerPage, BuildConfig.entriesPerPage);
		return response()
				.setEmbeds(ListEmbed.createHnsList(profileRepository, entries, page, pageCount))
				.setComponents(HNSListButtons.create(page, pageCount))
				.build();
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns list";
	}
}
