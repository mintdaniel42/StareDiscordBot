package org.mintdaniel42.starediscordbot.commands.misc;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.compose.exception.ComposeException;
import org.mintdaniel42.starediscordbot.data.Database;
import org.mintdaniel42.starediscordbot.data.exceptions.DatabaseException;
import org.mintdaniel42.starediscordbot.utils.Permission;

@RequiredArgsConstructor
@Singleton
@Slf4j
public final class ApproveChangeCommand extends BaseComposeCommand {
	@NonNull private final Database database;

	@Override
	public @NonNull MessageEditData compose(@NonNull final CommandContext context) throws ComposeException, DatabaseException {
		database.mergeRequest(requireIntegerOption(context, "id"));
		return response("request_was_successfully_merged");
	}

	@Override
	public @NonNull String getCommandId() {
		return "approve";
	}

	@Override
	public boolean hasPermission(@Nullable final Member member) {
		return Permission.hasP2(member);
	}
}
