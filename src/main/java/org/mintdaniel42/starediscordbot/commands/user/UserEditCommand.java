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
import org.mintdaniel42.starediscordbot.buttons.misc.ApproveButton;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.entity.RequestEntity;
import org.mintdaniel42.starediscordbot.data.entity.UserEntity;
import org.mintdaniel42.starediscordbot.data.repository.GroupRepository;
import org.mintdaniel42.starediscordbot.data.repository.RequestRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.*;

import java.util.UUID;

@RequiredArgsConstructor
public final class UserEditCommand implements CommandAdapter {
	@NonNull private final UserRepository userRepository;
	@NonNull private final RequestRepository requestRepository;
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final UsernameRepository usernameRepository;


	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof OptionMapping usernameMapping && event.getOptions().size() >= 2) {
			if (MCHelper.getUuid(usernameRepository, usernameMapping.getAsString()) instanceof UUID uuid) {
				final var userOptional = userRepository.selectByUUID(uuid);
				final var usernameOptional = usernameRepository.selectByUUID(uuid);
				if (userOptional.isPresent() && usernameOptional.isPresent()) {
					final var user = UserEntity.merge(event.getOptions(), userOptional.get().toBuilder());
					final var groupOptional = groupRepository.selectByTag(user.getGroupTag());
					if (!Permission.hasP2(event.getMember())) {
						long timestamp = System.currentTimeMillis();
						if (event.getGuild() instanceof Guild guild) {
							if (guild.getTextChannelById(Options.getRequestChannelId()) instanceof TextChannel requestChannel) {
								if (event.getMember() instanceof Member member) {
									if (requestRepository.insert(RequestEntity.from(timestamp, user)).equals(Status.SUCCESS)) {
										requestChannel.sendMessage(R.Strings.ui("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
														member.getAsMention(),
														timestamp))
												.setActionRow(ApproveButton.create(timestamp))
												.addEmbeds(UserEmbed.of(user, groupOptional.orElse(null), usernameOptional.get(), true))
												.queue();
										return interactionHook.editOriginal(R.Strings.ui("the_entry_change_was_successfully_requested"));
									} else
										return interactionHook.editOriginal(R.Strings.ui("the_entry_could_not_be_updated"));
								} else
									return interactionHook.editOriginal(R.Strings.ui("the_user_requesting_a_change_could_not_be_found"));
							} else
								return interactionHook.editOriginal(R.Strings.ui("the_request_channel_could_not_be_found"));
						} else return interactionHook.editOriginal(R.Strings.ui("the_guild_could_not_be_found"));
					} else if (!userRepository.update(user).equals(Status.SUCCESS)) {
						return interactionHook.editOriginal(R.Strings.ui("the_entry_could_not_be_updated"));
					} else return interactionHook.editOriginal(R.Strings.ui("the_entry_was_successfully_updated"))
							.setEmbeds(UserEmbed.of(user, groupOptional.orElse(null), usernameOptional.get(), true));
				} else return interactionHook.editOriginal(R.Strings.ui("this_user_entry_does_not_exist"));
			} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
