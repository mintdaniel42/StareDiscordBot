package org.mintdaniel42.starediscordbot.commands.pg;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.buttons.list.PGListButtons;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.entity.PGUserEntity;
import org.mintdaniel42.starediscordbot.data.repository.PGUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.List;

@RequiredArgsConstructor
public final class PGListCommand implements CommandAdapter {
	@NonNull private final PGUserRepository pgUserRepository;
	@NonNull private final UsernameRepository usernameRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		final int page;
		if (event.getOption("page") instanceof final OptionMapping pageMapping) {
			page = pageMapping.getAsInt() - 1;
		} else page = 0;
		final var pageCount = pgUserRepository.countPages();
		if (pageCount > page && page >= 0) {
			List<PGUserEntity> entries = pgUserRepository.selectByPage(page);
			return interactionHook.editOriginalEmbeds(ListEmbed.createPgList(usernameRepository, entries, page, pageCount))
					.setComponents(PGListButtons.create(page, pageCount));
		} else return interactionHook.editOriginal(R.Strings.ui("this_page_does_not_exist"));
	}
}
