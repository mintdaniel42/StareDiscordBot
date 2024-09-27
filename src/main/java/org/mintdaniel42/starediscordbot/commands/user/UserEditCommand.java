package org.mintdaniel42.starediscordbot.commands.user;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.buttons.misc.ApproveButton;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.compose.exception.ComposeException;
import org.mintdaniel42.starediscordbot.data.entity.RequestEntity;
import org.mintdaniel42.starediscordbot.data.entity.UserEntity;
import org.mintdaniel42.starediscordbot.data.exceptions.DatabaseException;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.RequestRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@Singleton
public final class UserEditCommand extends BaseComposeCommand {
	@NonNull private final UserRepository userRepository;
	@NonNull private final RequestRepository requestRepository;
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final ProfileRepository profileRepository;
	@NonNull private final BotConfig config;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws ComposeException, DatabaseException {
		requireOptionCount(context, 2);
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		final var user = UserEntity.merge(context.getOptions(), requireEntity(userRepository, profile.getUuid()).toBuilder());
		final var group = nullableEntity(groupRepository, user.getGroupTag()).orElse(null);
		if (!requirePermission(context.getMember(), Permission.p2)) {
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
					.setContent(R.Strings.ui("the_entry_was_successfully_updated"))
					.setEmbeds(UserEmbed.of(user, group, profile, false))
					.build();
		}
	}

	@Override
	public @NonNull String getCommandId() {
		return "user edit";
	}
}
