package org.mintdaniel42.starediscordbot.commands.misc;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.R;

@Factory
@RequiredArgsConstructor
@RequiresProperty(value = "feature.command.maintenance", equalTo = "true")
@Singleton
public final class MaintenanceCommand extends BaseComposeCommand {
	@NonNull private final BotConfig config;

	@Override
	protected @NonNull MessageEditData compose(@NonNull CommandContext context) throws BotException {
		final var active = requireBooleanOption(context, "active");
		config.setInMaintenance(active);
		final var presence = context.getPresence();
		if (active) {
			presence.setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.customStatus(R.Strings.ui("under_maintenance")));
		} else presence.setPresence(OnlineStatus.ONLINE, null);
		return response("maintenance_is_now_set_to_s", active);
	}

	@Bean
	@Named("maintenance")
	public SlashCommandData build() {
		return Commands.slash("maintenance", R.Strings.ui("control_maintenance"))
				.addOption(OptionType.BOOLEAN, "active", R.Strings.ui("if_maintenance_should_be_enabled"), true)
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
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
