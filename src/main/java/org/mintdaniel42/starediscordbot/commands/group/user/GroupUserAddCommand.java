package org.mintdaniel42.starediscordbot.commands.group.user;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@RequiresBean(UserGroup.class)
@RequiresProperty(value = "feature.command.group.user.add.enabled", equalTo = "true")
@Singleton
public final class GroupUserAddCommand extends BaseComposeCommand {
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final ProfileRepository profileRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		final var tag = requireStringOption(context, "tag");
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		final var user = requireEntity(userRepository, profile.getUuid());
		final var group = requireEntity(groupRepository, tag);
		userRepository.update(user.toBuilder()
				.groupTag(tag)
				.build());
		return response("the_user_s_was_added_to_the_group_s", profile.getUsername(), group.getName());
	}

	@Inject
	public void register(@NonNull @Named("group user") SubcommandGroupData group) {
		group.addSubcommands(new SubcommandData("add", R.Strings.ui("add_user_to_group"))
				.addOption(OptionType.STRING, "tag", R.Strings.ui("group_tag"), true, true)
				.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true));
	}

	@Override
	public @NonNull String getCommandId() {
		return "group user add";
	}

	@Override
	public boolean hasPermission(@NonNull final BotConfig config, @Nullable final Member member) {
		return Permission.hasP2(config, member);
	}
}
