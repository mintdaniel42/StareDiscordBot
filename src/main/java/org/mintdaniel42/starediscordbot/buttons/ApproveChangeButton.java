package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public class ApproveChangeButton extends ListenerAdapter {
	@NonNull final DatabaseAdapter databaseAdapter;

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		String[] buttonParts = event.getComponentId().split(":");
		if (buttonParts[0].equals("approve") && buttonParts.length == 2) {
			if (!Options.isInMaintenance()) {
				if (DCHelper.hasRole(event.getMember(), Options.getEditRoleId()) || DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
					if (databaseAdapter.mergeRequest(Long.parseLong(buttonParts[1]))) {
						event.reply(R.string("request_was_successfully_merged")).queue();
					} else event.reply(R.string("request_could_not_be_merged")).queue();
				} else event.reply(R.string("you_do_not_have_the_permission_to_use_this_button")).queue();
			} else event.reply(R.string("the_bot_is_currently_in_maintenance_mode")).queue();
		}
	}

	@Contract(pure = true, value = "_ -> new")
	public static @NonNull ActionRow create(final long id) {
		return ActionRow.of(
				Button.primary(
						"approve:%s".formatted(id),
						R.string("approve_this_change")
				)
		);
	}
}
