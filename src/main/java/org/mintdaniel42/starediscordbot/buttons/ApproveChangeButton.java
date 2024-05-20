package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public final class ApproveChangeButton extends ListenerAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		String[] buttonParts = event.getComponentId().split(":");
		if (!buttonParts[0].equals("approve") || buttonParts.length != 2) return;

		// check maintenance
		if (Options.isInMaintenance()) {
			event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
			return;
		}

		// check permission level
		if (DCHelper.lacksRole(event.getMember(), Options.getEditRoleId()) && DCHelper.lacksRole(event.getMember(), Options.getCreateRoleId())) {
			event.reply(R.string("you_do_not_have_the_permission_to_use_this_button")).queue();
			return;
		}

		// try to merge the change
		long id = Long.parseLong(buttonParts[1]);
		if ((id != -1) && databaseAdapter.mergeRequest(id)) {
			event.reply(R.string("request_was_successfully_merged")).queue();
		} else {
			event.reply(R.string("request_could_not_be_merged")).queue();
		}
	}
}
