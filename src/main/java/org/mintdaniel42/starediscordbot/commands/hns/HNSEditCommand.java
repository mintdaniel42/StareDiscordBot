package org.mintdaniel42.starediscordbot.commands.hns;

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
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.ProfileRepository;
import org.mintdaniel42.starediscordbot.data.repository.RequestRepository;
import org.mintdaniel42.starediscordbot.embeds.user.hns.HNSFullUserEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
@RequiresBean(HNSCommand.class)
@RequiresProperty(value = "feature.command.hns.edit.enabled", equalTo = "true")
@Singleton
public final class HNSEditCommand extends BaseComposeCommand {
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final RequestRepository requestRepository;
	@NonNull private final ProfileRepository profileRepository;
	@NonNull private final BotConfig config;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		requireOptionCount(context, 2);
		final var profile = requireProfile(profileRepository, requireStringOption(context, "username"));
		final var hnsUser = merge(context, requireEntity(hnsUserRepository, profile.getUuid()).toBuilder());
		if (!requirePermission(config, context.getMember(), Permission.p2)) {
			final var timestamp = System.currentTimeMillis();
			final var requestChannel = requireChannel(context, config.getGuildId(), config.getRequestChannelId());
			requestRepository.insert(RequestEntity.from(timestamp, hnsUser));
			requestChannel.sendMessage(R.Strings.ui("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
							context.getMember().getAsMention(),
							timestamp))
					.setActionRow(ApproveButton.create(timestamp))
					.addEmbeds(HNSFullUserEmbed.of(hnsUser, profile, true))
					.queue();
			return response("the_entry_change_was_successfully_requested");
		} else {
			hnsUserRepository.update(hnsUser);
			return response()
					.setText(R.Strings.ui("the_entry_was_successfully_updated"))
					.addEmbed(HNSFullUserEmbed.of(hnsUser, profile, false))
					.compose();
		}
	}

	@Inject
	public void register(@NonNull @Named("hns") SlashCommandData command) {
		command.addSubcommands(new SubcommandData("edit", R.Strings.ui("edit_a_hide_n_seek_entry"))
				.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
				.addOption(OptionType.NUMBER, "points", R.Strings.ui("points"), false, true)
				.addOption(OptionType.STRING, "rating", R.Strings.ui("rating"))
				.addOption(OptionType.STRING, "joined", R.Strings.ui("joined"))
				.addOption(OptionType.BOOLEAN, "secondary", R.Strings.ui("secondary"))
				.addOption(OptionType.BOOLEAN, "banned", R.Strings.ui("banned"))
				.addOption(OptionType.BOOLEAN, "cheating", R.Strings.ui("cheating"))
				.addOption(OptionType.STRING, "top10", R.Strings.ui("top10"))
				.addOption(OptionType.INTEGER, "streak", R.Strings.ui("streak"))
				.addOption(OptionType.STRING, "highest_rank", R.Strings.ui("highest_rank")));
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns edit";
	}
}
