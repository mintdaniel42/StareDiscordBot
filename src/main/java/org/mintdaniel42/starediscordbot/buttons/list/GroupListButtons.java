package org.mintdaniel42.starediscordbot.buttons.list;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.buttons.ButtonAdapter;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.GroupModel;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public final class GroupListButtons implements ButtonAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Contract(pure = true, value = "_, _, _ -> new")
	public static @NonNull ActionRow create(@NonNull final GroupModel groupModel, final int page, final long maxPages) {
		return ActionRow.of(
				Button.primary(
								"group:%s:%s".formatted(groupModel.getTag(), page - 1),
								R.Strings.ui("previous_page")
						).withEmoji(R.Emojis.arrowLeft)
						.withDisabled(page <= 0),
				Button.primary(
								"group:%s:%s".formatted(groupModel.getTag(), page + 1),
								R.Strings.ui("next_page")
						).withEmoji(R.Emojis.arrowRight)
						.withDisabled(page >= maxPages - 1)
		);
	}


	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) {
		final var buttonParts = event.getComponentId().split(":");
		final var page = Integer.parseInt(buttonParts[2]);
		if (databaseAdapter.getGroup(buttonParts[1]) instanceof final GroupModel groupModel) {
			return interactionHook.editOriginalEmbeds(GroupEmbed.of(databaseAdapter, groupModel, page))
					.setComponents(GroupListButtons.create(groupModel, page, databaseAdapter.getGroupMemberPages(groupModel.getTag())));
		} else return interactionHook.editOriginal(R.Strings.ui("this_page_does_not_exist"));
	}
}
