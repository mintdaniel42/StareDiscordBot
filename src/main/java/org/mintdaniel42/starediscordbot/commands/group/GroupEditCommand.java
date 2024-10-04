package org.mintdaniel42.starediscordbot.commands.group;

import io.avaje.inject.RequiresBean;
import io.avaje.inject.RequiresProperty;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.mintdaniel42.starediscordbot.buttons.misc.ApproveButton;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.data.entity.GroupEntity;
import org.mintdaniel42.starediscordbot.data.entity.RequestEntity;
import org.mintdaniel42.starediscordbot.data.repository.*;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.Permission;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
@RequiresBean(GroupCommand.class)
@RequiresProperty(value = "feature.command.group.edit.enabled", equalTo = "true")
@Singleton
public final class GroupEditCommand extends BaseComposeCommand {
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final RequestRepository requestRepository;
	@NonNull private final ProfileRepository profileRepository;
	@NonNull private final BotConfig config;

	@Override
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		requireOptionCount(context, 2);
		final var leaderName = nullableStringOption(context, "leader").orElse(null);
		UUID leaderUUID = null;
		if (leaderName != null) leaderUUID = requireProfile(profileRepository, leaderName).getUuid();
		final var group = merge(context, requireEntity(groupRepository, requireStringOption(context, "tag")).toBuilder(), leaderUUID);
		if (!requirePermission(config, context.getMember(), Permission.p2)) {
			final var timestamp = System.currentTimeMillis();
			final var requestChannel = requireChannel(context, config.getGuildId(), config.getRequestChannelId());
			requestRepository.insert(RequestEntity.from(timestamp, group));
			requestChannel.sendMessage(R.Strings.ui("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
							context.getMember().getAsMention(),
							timestamp))
					.setActionRow(ApproveButton.create(timestamp))
					.addEmbeds(GroupEmbed.of(group, userRepository, hnsUserRepository, profileRepository, true))
					.queue();
			return response("the_entry_change_was_successfully_requested");
		} else {
			groupRepository.update(group);
			return response()
					.setContent(R.Strings.ui("the_entry_was_successfully_updated"))
					.setEmbeds(GroupEmbed.of(group, userRepository, hnsUserRepository, profileRepository))
					.build();
		}
	}

	@Inject
	public void register(@NonNull @Named("group") SlashCommandData command) {
		command.addSubcommands(new SubcommandData("edit", R.Strings.ui("edit_group"))
				.addOption(OptionType.STRING, "tag", R.Strings.ui("group_tag"), true, true)
				.addOption(OptionType.STRING, "name", R.Strings.ui("group_name"))
				.addOption(OptionType.STRING, "leader", R.Strings.ui("group_leader"), false, true)
				.addOptions(new OptionData(OptionType.STRING, "relation", R.Strings.ui("group_relation"))
						.addChoice(R.Strings.ui("enemy"), GroupEntity.Relation.enemy.name())
						.addChoice(R.Strings.ui("neutral"), GroupEntity.Relation.neutral.name())
						.addChoice(R.Strings.ui("ally"), GroupEntity.Relation.ally.name())));
	}

	@Override
	public @NonNull String getCommandId() {
		return "group edit";
	}
}
