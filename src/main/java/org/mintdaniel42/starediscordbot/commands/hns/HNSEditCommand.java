package org.mintdaniel42.starediscordbot.commands.hns;

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
import org.mintdaniel42.starediscordbot.buttons.ApproveButton;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.HNSUserModel;
import org.mintdaniel42.starediscordbot.data.RequestModel;
import org.mintdaniel42.starediscordbot.data.UserModel;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.DCHelper;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
public class HNSEditCommand implements CommandAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof final OptionMapping usernameMapping &&
				event.getOptions().size() >= 2) {
			if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof final UUID uuid) {
				if (databaseAdapter.getHnsUser(uuid) instanceof HNSUserModel hnsUserModel &&
						databaseAdapter.getUser(uuid) instanceof UserModel userModel) {
					hnsUserModel = HNSUserModel.merge(event.getOptions(), hnsUserModel.toBuilder());
					userModel = userModel.toBuilder().hnsUser(hnsUserModel).build();

					if (!DCHelper.hasRole(event.getMember(), Options.getEditRoleId()) && !DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
						long timestamp = System.currentTimeMillis();
						if (event.getGuild() instanceof Guild guild) {
							if (guild.getTextChannelById(Options.getRequestChannelId()) instanceof TextChannel requestChannel) {
								if (event.getMember() instanceof Member member) {
									if (databaseAdapter.addRequest(RequestModel.from(timestamp, hnsUserModel))) {
										requestChannel.sendMessage(R.Strings.ui("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
														member.getAsMention(),
														timestamp))
												.setComponents(ApproveButton.create(timestamp))
												.addEmbeds(UserEmbed.of(userModel, UserEmbed.Type.HNS_ALL, true)).queue();
										return interactionHook.editOriginal(R.Strings.ui("the_entry_change_was_successfully_requested"));
									} else
										return interactionHook.editOriginal(R.Strings.ui("the_entry_could_not_be_updated"));
								}
							}
						}
					} else if (!databaseAdapter.edit(hnsUserModel)) {
						return interactionHook.editOriginal(R.Strings.ui("the_entry_could_not_be_updated"));
					} else return interactionHook.editOriginal(R.Strings.ui("the_entry_was_successfully_updated"))
							.setEmbeds(UserEmbed.of(userModel, UserEmbed.Type.HNS_ALL));
				} else return interactionHook.editOriginal(R.Strings.ui("this_user_entry_does_not_exist"));
			} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
		return interactionHook.editOriginal(R.Strings.ui("an_impossible_error_occurred")); // TODO more detailed messages
	}
}