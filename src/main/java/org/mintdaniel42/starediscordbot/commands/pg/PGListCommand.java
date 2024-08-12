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
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.PGUserModel;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.List;

@RequiredArgsConstructor
public final class PGListCommand implements CommandAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		final int page;
		if (event.getOption("page") instanceof final OptionMapping pageMapping) {
			page = pageMapping.getAsInt() - 1;
		} else page = 0;
		if (databaseAdapter.getPgPages() > page && page >= 0) {
			if (databaseAdapter.getPgUserList(page) instanceof final List<PGUserModel> entries && !entries.isEmpty()) {
				return interactionHook.editOriginalEmbeds(ListEmbed.createPgList(databaseAdapter, entries, page))
						.setComponents(PGListButtons.create(page, databaseAdapter.getPgPages()));
			} else return interactionHook.editOriginal(R.Strings.ui("no_entries_available"));
		} else return interactionHook.editOriginal(R.Strings.ui("this_page_does_not_exist"));
	}
}
