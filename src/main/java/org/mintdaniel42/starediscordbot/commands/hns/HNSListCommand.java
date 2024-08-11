package org.mintdaniel42.starediscordbot.commands.hns;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.HNSUserModel;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.List;

@RequiredArgsConstructor
public final class HNSListCommand implements CommandAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		final int page;
		if (event.getOption("page") instanceof final OptionMapping pageMapping) {
			page = pageMapping.getAsInt();
		} else page = 0;
		if (databaseAdapter.getHnsUserList(page) instanceof final List<HNSUserModel> entries && !entries.isEmpty()) {
			if (databaseAdapter.getHnsPages() > page && page >= 0) {
				return interactionHook.editOriginalEmbeds(ListEmbed.createHnsList(databaseAdapter, entries, page))
						.setComponents(ListButtons.create(ListButtons.Type.hns, page, databaseAdapter.getHnsPages()));
			} else return interactionHook.editOriginal(R.Strings.ui("this_page_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("no_entries_available"));
	}
}
