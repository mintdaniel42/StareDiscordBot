package org.mintdaniel42.starediscordbot.commands.group;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.compose.exception.ComposeException;
import org.mintdaniel42.starediscordbot.data.exceptions.DatabaseException;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@Singleton
public final class GroupDeleteCommand extends BaseComposeCommand {
	@NonNull private final GroupRepository groupRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws ComposeException, DatabaseException {
		final var tag = requireStringOption(context, "tag");
		groupRepository.deleteById(tag);
		return response()
				.setContent(R.Strings.ui("the_group_was_successfully_deleted"))
				.build();
	}

	@Override
	public @NonNull String getCommandId() {
		return "group delete";
	}

	@Override
	public boolean hasPermission(@Nullable final Member member) {
		return Permission.hasP4(member);
	}
}
