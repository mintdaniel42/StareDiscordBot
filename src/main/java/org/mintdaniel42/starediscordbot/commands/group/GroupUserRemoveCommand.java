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
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.utils.Permission;

@RequiredArgsConstructor
@Singleton
public final class GroupUserRemoveCommand extends BaseComposeCommand {
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final ProfileRepository profileRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws ComposeException, DatabaseException {
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		final var user = requireEntity(userRepository, profile.getUuid());
		if (user.getGroupTag() == null) {
			return response("the_user_s_is_not_in_any_group", profile.getUsername());
		} else {
			final var group = requireEntity(groupRepository, user.getGroupTag());
			userRepository.update(user.toBuilder()
					.groupTag(null)
					.build());
			return response("the_user_s_was_removed_from_the_group_s", profile.getUsername(), group.getName());
		}
	}

	@Override
	public @NonNull String getCommandId() {
		return "group user remove";
	}

	@Override
	public boolean hasPermission(@Nullable final Member member) {
		return Permission.hasP2(member);
	}
}
