package org.mintdaniel42.starediscordbot.commands.pg;

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
import org.mintdaniel42.starediscordbot.data.repository.PGUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.RequestRepository;
import org.mintdaniel42.starediscordbot.embeds.user.pg.PGUserEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@RequiresBean(PGCommand.class)
@RequiresProperty(value = "feature.command.pg.edit.enabled", equalTo = "true")
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
		final var pgUser = merge(context, requireEntity(pgUserRepository, profile.getUuid()).toBuilder());
		if (!requirePermission(config, context.getMember(), Permission.p2)) {
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

	@Inject
	public void register(@NonNull @Named("pg") SlashCommandData command) {
		command.addSubcommands(new SubcommandData("edit", R.Strings.ui("edit_a_partygames_entry"))
				.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
				.addOption(OptionType.NUMBER, "points", R.Strings.ui("points"), false, true)
				.addOption(OptionType.STRING, "rating", R.Strings.ui("rating"))
				.addOption(OptionType.STRING, "joined", R.Strings.ui("joined"))
				.addOption(OptionType.NUMBER, "luck", R.Strings.ui("luck"), false, true)
				.addOption(OptionType.NUMBER, "quota", R.Strings.ui("quota"))
				.addOption(OptionType.NUMBER, "winrate", R.Strings.ui("winrate")));
	}

	@Override
	public @NonNull String getCommandId() {
		return "pg edit";
	}
}
