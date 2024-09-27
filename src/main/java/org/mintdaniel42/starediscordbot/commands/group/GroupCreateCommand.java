package org.mintdaniel42.starediscordbot.commands.group;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.entity.GroupEntity;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@Singleton
public final class GroupCreateCommand extends BaseComposeCommand {
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final ProfileRepository profileRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		final var tag = requireStringOption(context, "tag");
		final var leaderProfile = requireProfile(profileRepository, requireStringOption(context, "leader"));
		final var name = requireStringOption(context, "name");
		final var relation = requireStringOption(context, "relation", GroupEntity.Relation::valueOf);
		final var group = GroupEntity.builder()
				.tag(tag)
				.name(name)
				.leader(leaderProfile.getUuid())
				.relation(relation)
				.build();
		groupRepository.insert(group);
		return response()
				.setContent(R.Strings.ui("the_group_was_successfully_created"))
				.setEmbeds(GroupEmbed.of(group, userRepository, hnsUserRepository, profileRepository, 0, false))
				.build();
	}

	@Override
	public @NonNull String getCommandId() {
		return "group create";
	}

	@Override
	public boolean hasPermission(@Nullable final Member member) {
		return Permission.hasP4(member);
	}
}
