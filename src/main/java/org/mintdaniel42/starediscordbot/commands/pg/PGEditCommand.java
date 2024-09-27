package org.mintdaniel42.starediscordbot.commands.pg;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.buttons.misc.ApproveButton;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.entity.PGUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.RequestEntity;
import org.mintdaniel42.starediscordbot.data.repository.PGUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.RequestRepository;
import org.mintdaniel42.starediscordbot.embeds.user.pg.PGUserEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@Singleton
public final class PGEditCommand extends BaseComposeCommand {
	@NonNull private final PGUserRepository pgUserRepository;
	@NonNull private final RequestRepository requestRepository;
	@NonNull private final ProfileRepository profileRepository;
	@NonNull private final BotConfig config;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		requireOptionCount(context, 2);
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		final var pgUser = PGUserEntity.merge(context.getOptions(), requireEntity(pgUserRepository, profile.getUuid()).toBuilder());
		if (!requirePermission(context.getMember(), Permission.p2)) {
			final var timestamp = System.currentTimeMillis();
			final var requestChannel = requireChannel(context, config.getGuildId(), config.getRequestChannelId());
			requestRepository.insert(RequestEntity.from(timestamp, pgUser));
			requestChannel.sendMessage(R.Strings.ui("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
							context.getMember().getAsMention(),
							timestamp))
					.setActionRow(ApproveButton.create(timestamp))
					.addEmbeds(PGUserEmbed.of(pgUser, profile, true))
					.queue();
			return response("the_entry_change_was_successfully_requested");
		} else {
			pgUserRepository.update(pgUser);
			return response()
					.setContent(R.Strings.ui("the_entry_was_successfully_updated"))
					.setEmbeds(PGUserEmbed.of(pgUser, profile, false))
					.build();
		}
	}

	@Override
	public @NonNull String getCommandId() {
		return "pg edit";
	}
}
