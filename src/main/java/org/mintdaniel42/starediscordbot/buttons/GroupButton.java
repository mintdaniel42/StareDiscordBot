package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.GroupModel;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@Slf4j
@RequiredArgsConstructor
public final class GroupButton extends ListenerAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		String[] buttonParts = event.getComponentId().split(":");
		if (!buttonParts[0].equals("group") || buttonParts.length != 2) return;

		if (!Options.isInMaintenance()) {
			if (databaseAdapter.getGroup(buttonParts[1]) instanceof GroupModel groupModel) {
				event.deferReply().queue(interactionHook -> interactionHook.editOriginalEmbeds(GroupEmbed.of(databaseAdapter, groupModel, 0))
						.setComponents(ListButtons.create(groupModel, 0, databaseAdapter.getGroupMemberPages(groupModel.getTag())))
						.queue());
			} else event.reply(R.Strings.ui("this_group_does_not_exist")).queue();
		} else event.reply(R.Strings.ui("the_bot_is_currently_in_maintenance_mode")).queue();
	}
}
