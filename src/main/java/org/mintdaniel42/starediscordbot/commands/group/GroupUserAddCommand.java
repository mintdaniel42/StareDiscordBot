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
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@Singleton
public final class GroupUserAddCommand extends BaseComposeCommand {
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final ProfileRepository profileRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws ComposeException, DatabaseException {
		final var tag = requireStringOption(context, "tag");
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		final var user = requireEntity(userRepository, profile.getUuid());
		final var group = requireEntity(groupRepository, tag);
		userRepository.update(user.toBuilder()
				.groupTag(tag)
				.build());
		return response()
				.setContent(R.Strings.ui("the_user_s_was_added_to_the_group_s",
						MCHelper.getUsername(profileRepository, profile.getUuid()),
						group.getName()))
				.build();
	}

	@Override
	public @NonNull String getCommandId() {
		return "group user add";
	}

	@Override
	public boolean hasPermission(@Nullable final Member member) {
		return Permission.hasP2(member);
	}
}
