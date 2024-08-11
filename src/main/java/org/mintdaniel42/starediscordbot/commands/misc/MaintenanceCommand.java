package org.mintdaniel42.starediscordbot.commands.misc;

import lombok.NonNull;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

public final class MaintenanceCommand implements CommandAdapter {
	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("active") instanceof final OptionMapping activeMapping) {
			Options.setInMaintenance(activeMapping.getAsBoolean());
			if (activeMapping.getAsBoolean()) {
				event.getJDA()
						.getPresence()
						.setPresence(OnlineStatus.DO_NOT_DISTURB,
								Activity.customStatus(R.Strings.ui("under_maintenance")));
			} else event.getJDA().getPresence().setPresence(null, null);
			return interactionHook.editOriginal(R.Strings.ui("maintenance_is_now_set_to_s", activeMapping.getAsBoolean()));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
