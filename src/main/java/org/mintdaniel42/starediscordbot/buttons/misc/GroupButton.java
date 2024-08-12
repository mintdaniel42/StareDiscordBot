package org.mintdaniel42.starediscordbot.buttons.misc;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.buttons.ButtonAdapter;
import org.mintdaniel42.starediscordbot.buttons.list.GroupListButtons;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.GroupModel;
import org.mintdaniel42.starediscordbot.data.UserModel;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public final class GroupButton implements ButtonAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	public static @NonNull Button create(@NonNull final UserModel userModel) {
		return Button.primary(
				String.format("group:%s", userModel.getGroup() != null ? userModel.getGroup().getTag() : null),
						R.Strings.ui("show_group"))
				.withEmoji(R.Emojis.group)
				.withDisabled(userModel.getGroup() == null);
	}

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final ButtonInteractionEvent event) {
		if (databaseAdapter.getGroup(event.getComponentId().split(":")[1]) instanceof final GroupModel groupModel) {
			return interactionHook.editOriginalEmbeds(GroupEmbed.of(databaseAdapter, groupModel, 0))
					.setComponents(GroupListButtons.create(groupModel, 0, databaseAdapter.getGroupMemberPages(groupModel.getTag())));
		} else return interactionHook.editOriginal(R.Strings.ui("this_group_does_not_exist"));
	}
}
