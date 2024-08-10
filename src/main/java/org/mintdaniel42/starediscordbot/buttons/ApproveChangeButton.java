package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public final class ApproveChangeButton extends ListenerAdapter {
	@NonNull final DatabaseAdapter databaseAdapter;

	@Contract(pure = true, value = "_ -> new")
	public static @NonNull ActionRow create(final long id) {
		return ActionRow.of(Button.primary(
						"approve:%s".formatted(id),
				R.Strings.ui("approve_this_change")
				).withDisabled(id == -1)
		);
	}

	@Override
	public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
		String[] buttonParts = event.getComponentId().split(":");
		if (buttonParts[0].equals("approve") && buttonParts.length == 2) {
			if (!Options.isInMaintenance()) {
				if (DCHelper.hasRole(event.getMember(), Options.getEditRoleId()) || DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
					if (databaseAdapter.mergeRequest(Long.parseLong(buttonParts[1]))) {
						event.deferEdit()
								.queue(interactionHook -> interactionHook.editOriginalComponents(create(-1))
										.queue());
					} else event.reply(R.Strings.ui("request_could_not_be_merged")).queue();
				} else event.reply(R.Strings.ui("you_do_not_have_the_permission_to_use_this_button")).queue();
			} else event.reply(R.Strings.ui("the_bot_is_currently_in_maintenance_mode")).queue();
		}
	}
}
