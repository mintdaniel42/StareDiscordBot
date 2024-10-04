package org.mintdaniel42.starediscordbot.commands.misc;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.Database;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@Factory
@RequiredArgsConstructor
@RequiresProperty(value = "feature.command.approve", equalTo = "true")
@Singleton
@Slf4j
public final class ApproveChangeCommand extends BaseComposeCommand {
	@NonNull private final Database database;

	@Override
	public @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		database.mergeRequest(requireIntegerOption(context, "id"));
		return response("request_was_successfully_merged");
	}

	@Bean
	@Named("approve")
	public SlashCommandData build() {
		return Commands.slash("approve", R.Strings.ui("approve_a_change"))
				.addOption(OptionType.INTEGER, "id", R.Strings.ui("change_id"), true, true);
	}

	@Override
	public @NonNull String getCommandId() {
		return "approve";
	}

	@Override
	public boolean hasPermission(@NonNull final BotConfig config, @Nullable final Member member) {
		return Permission.hasP2(config, member);
	}
}
