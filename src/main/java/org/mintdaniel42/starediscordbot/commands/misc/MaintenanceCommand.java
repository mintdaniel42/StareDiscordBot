package org.mintdaniel42.starediscordbot.commands.misc;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.compose.exception.ComposeException;
import org.mintdaniel42.starediscordbot.data.exceptions.DatabaseException;
import org.mintdaniel42.starediscordbot.utils.R;

@Singleton
@RequiredArgsConstructor
public final class MaintenanceCommand extends BaseComposeCommand {
	@NonNull private final BotConfig config;

	@Override
	protected @NonNull MessageEditData compose(@NonNull CommandContext context) throws ComposeException, DatabaseException {
		final var active = requireBooleanOption(context, "active");
		config.setInMaintenance(active);
		final var presence = context.getPresence();
		if (active) {
			presence.setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.customStatus(R.Strings.ui("under_maintenance")));
		} else presence.setPresence(null, null);
		return response("maintenance_is_now_set_to_s", active);
	}

	@Override
	public @NonNull String getCommandId() {
		return "maintenance";
	}

	@Override
	public boolean isPublicResponseRestricted() {
		return true;
	}
}
