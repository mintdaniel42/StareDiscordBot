package org.mintdaniel42.starediscordbot.commands.user;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.buttons.misc.ApproveButton;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.entity.RequestEntity;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.RequestRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@RequiresBean(UserCommand.class)
@RequiresProperty(value = "feature.command.user.edit.enabled", equalTo = "true")
@Singleton
public final class UserEditCommand extends BaseComposeCommand {
	@NonNull private final UserRepository userRepository;
	@NonNull private final RequestRepository requestRepository;
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final ProfileRepository profileRepository;
	@NonNull private final BotConfig config;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		requireOptionCount(context, 2);
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		final var user = merge(context, requireEntity(userRepository, profile.getUuid()).toBuilder());
		final var group = nullableEntity(groupRepository, user.getGroupTag()).orElse(null);
		if (!requirePermission(config, context.getMember(), Permission.p2)) {
			final var timestamp = System.currentTimeMillis();
			final var requestChannel = requireChannel(context, config.getGuildId(), config.getRequestChannelId());
			requestRepository.insert(RequestEntity.from(timestamp, user));
			requestChannel.sendMessage(R.Strings.ui("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
							context.getMember().getAsMention(),
							timestamp))
					.setActionRow(ApproveButton.create(timestamp))
					.addEmbeds(UserEmbed.of(user, group, profile, true))
					.queue();
			return response("the_entry_change_was_successfully_requested");
		} else {
			userRepository.update(user);
			return response()
					.setText(R.Strings.ui("the_entry_was_successfully_updated"))
					.addEmbed(UserEmbed.of(user, group, profile, false))
					.compose();
		}
	}

	@Inject
	public void register(@NonNull @Named("user") SlashCommandData command) {
		command.addSubcommands(new SubcommandData("edit", R.Strings.ui("edit_a_user_entry"))
				.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
				.addOption(OptionType.STRING, "note", R.Strings.ui("note"))
				.addOption(OptionType.USER, "discord", R.Strings.ui("discord_tag")));
	}

	@Override
	public @NonNull String getCommandId() {
		return "user edit";
	}
}
