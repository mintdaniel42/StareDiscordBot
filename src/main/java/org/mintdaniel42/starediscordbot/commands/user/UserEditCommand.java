package org.mintdaniel42.starediscordbot.commands.user;

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
import org.mintdaniel42.starediscordbot.buttons.ApproveChangeButton;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.commands.CommandDispatcher;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.RequestModel;
import org.mintdaniel42.starediscordbot.data.UserModel;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
public class UserEditCommand implements CommandAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof OptionMapping usernameMapping && event.getOptions().size() >= 2) {
			if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof UUID uuid) {
				if (databaseAdapter.getUser(uuid) instanceof UserModel userModel) {
					userModel = buildUserModel(event, userModel.toBuilder());

					if (!CommandDispatcher.canEdit(event.getMember())) {
						long timestamp = System.currentTimeMillis();
						if (event.getGuild() instanceof Guild guild) {
							if (guild.getTextChannelById(Options.getRequestChannelId()) instanceof TextChannel requestChannel) {
								if (event.getMember() instanceof Member member) {
									if (databaseAdapter.addRequest(RequestModel.from(timestamp, userModel))) {
										requestChannel.sendMessage(R.Strings.ui("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
														member.getAsMention(),
														timestamp))
												.setComponents(ApproveChangeButton.create(timestamp))
												.addEmbeds(UserEmbed.of(userModel, UserEmbed.Type.BASE, true))
												.queue();
										return interactionHook.editOriginal(R.Strings.ui("the_entry_change_was_successfully_requested"));
									} else
										return interactionHook.editOriginal(R.Strings.ui("the_entry_could_not_be_updated"));
								}
							}
						}
					} else if (!databaseAdapter.edit(userModel)) {
						return interactionHook.editOriginal(R.Strings.ui("the_entry_could_not_be_updated"));
					} else return interactionHook.editOriginal(R.Strings.ui("the_entry_was_successfully_updated"))
							.setEmbeds(UserEmbed.of(userModel, UserEmbed.Type.BASE));
				} else return interactionHook.editOriginal(R.Strings.ui("this_user_entry_does_not_exist"));
			} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
		return interactionHook.editOriginal(R.Strings.ui("an_impossible_error_occurred")); // TODO more detailed messages
	}

	private @NonNull UserModel buildUserModel(@NonNull final SlashCommandInteractionEvent event, UserModel.UserModelBuilder userBuilder) {
		for (OptionMapping optionMapping : event.getOptions()) {
			switch (optionMapping.getName()) {
				case "discord" -> userBuilder.discord(optionMapping.getAsLong());
				case "note" -> userBuilder.note(optionMapping.getAsString());
			}
		}

		return userBuilder.build();
	}
}
