package org.mintdaniel42.starediscordbot.commands.group;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.buttons.misc.ApproveButton;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.GroupModel;
import org.mintdaniel42.starediscordbot.data.RequestModel;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.Permissions;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
public final class GroupEditCommand implements CommandAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("tag") instanceof final OptionMapping tagMapping && event.getOptions().size() >= 2) {
			if (databaseAdapter.getGroup(tagMapping.getAsString()) instanceof GroupModel groupModel) {
				UUID leaderUuid = null;
				if (!(event.getOption("leader") instanceof final OptionMapping leaderMapping) ||
						(leaderUuid = MCHelper.getUuid(databaseAdapter, leaderMapping.getAsString())) != null) {
					groupModel = GroupModel.merge(event.getOptions(), groupModel.toBuilder(), leaderUuid);

					if (!Permissions.edit(event.getMember())) {
						long timestamp = System.currentTimeMillis();
						if (event.getGuild() instanceof final Guild guild) {
							if (guild.getTextChannelById(Options.getRequestChannelId()) instanceof final TextChannel requestChannel) {
								if (event.getMember() instanceof final Member member) {
									if (databaseAdapter.addRequest(RequestModel.from(timestamp, groupModel))) {
										requestChannel.sendMessage(R.Strings.ui("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
														member.getAsMention(),
														timestamp))
												.setActionRow(ApproveButton.create(timestamp))
												.addEmbeds(GroupEmbed.of(databaseAdapter, groupModel, 0, true));
										return interactionHook.editOriginal(R.Strings.ui("the_entry_change_was_successfully_requested"));
									} else
										return interactionHook.editOriginal(R.Strings.ui("the_entry_could_not_be_updated"));
								} else
									return interactionHook.editOriginal(R.Strings.ui("the_user_requesting_a_change_could_not_be_found"));
							} else
								return interactionHook.editOriginal(R.Strings.ui("the_request_channel_could_not_be_found"));
						} else return interactionHook.editOriginal(R.Strings.ui("the_guild_could_not_be_found"));
					} else if (!databaseAdapter.edit(groupModel)) {
						return interactionHook.editOriginal(R.Strings.ui("the_entry_could_not_be_updated"));
					} else return interactionHook.editOriginal(R.Strings.ui("the_entry_was_successfully_updated"))
							.setEmbeds(GroupEmbed.of(databaseAdapter, groupModel, 0));
				} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
			} else return interactionHook.editOriginal(R.Strings.ui("this_group_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
