package org.mintdaniel42.starediscordbot.commands.group.user;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.buttons.list.GroupListButtons;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@RequiresBean(UserGroup.class)
@RequiresProperty(value = "feature.command.group.user.show.enabled", equalTo = "true")
@Singleton
public final class GroupUserShowCommand extends BaseComposeCommand {
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final ProfileRepository profileRepository;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		final var user = requireEntity(userRepository, profile.getUuid());
		if (user.getGroupTag() != null) {
			final var group = requireEntity(groupRepository, user.getGroupTag());
			return response()
					.setEmbeds(GroupEmbed.of(group, userRepository, hnsUserRepository, profileRepository))
					.setComponents(GroupListButtons.create(group, 0, (long) Math.ceil((double) userRepository.selectByGroupTag(group.getTag()).size() / BuildConfig.entriesPerPage)))
					.build();
		} else return response("the_user_s_is_not_in_any_group", profile.getUsername());
	}

	@Inject
	public void register(@NonNull @Named("group user") SubcommandGroupData group) {
		group.addSubcommands(new SubcommandData("show", R.Strings.ui("show_group_of_user"))
				.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true));
	}

	@Override
	public @NonNull String getCommandId() {
		return "group user show";
	}
}