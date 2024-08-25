package org.mintdaniel42.starediscordbot.commands.group;

import jakarta.inject.Singleton;
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
import org.mintdaniel42.starediscordbot.data.entity.GroupEntity;
import org.mintdaniel42.starediscordbot.data.entity.RequestEntity;
import org.mintdaniel42.starediscordbot.data.repository.*;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.utils.*;

import java.util.UUID;

@RequiredArgsConstructor
@Singleton
public final class GroupEditCommand implements CommandAdapter {
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final RequestRepository requestRepository;
	@NonNull private final UsernameRepository usernameRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("tag") instanceof final OptionMapping tagMapping && event.getOptions().size() >= 2) {
			final var groupOptional = groupRepository.selectById(tagMapping.getAsString());
			if (groupOptional.isPresent()) {
				UUID leaderUuid = null;
				if (!(event.getOption("leader") instanceof final OptionMapping leaderMapping) ||
						(leaderUuid = MCHelper.getUuid(usernameRepository, leaderMapping.getAsString())) != null) {
					final var group = GroupEntity.merge(event.getOptions(), groupOptional.get().toBuilder(), leaderUuid);

					if (!Permission.hasP2(event.getMember())) {
						long timestamp = System.currentTimeMillis();
						if (event.getGuild() instanceof final Guild guild) {
							if (guild.getTextChannelById(Options.getRequestChannelId()) instanceof final TextChannel requestChannel) {
								if (event.getMember() instanceof final Member member) {
									if (requestRepository.insert(RequestEntity.from(timestamp, group)).equals(Status.SUCCESS)) {
										requestChannel.sendMessage(R.Strings.ui("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
														member.getAsMention(),
														timestamp))
												.setActionRow(ApproveButton.create(timestamp))
												.addEmbeds(GroupEmbed.of(group, userRepository, hnsUserRepository, usernameRepository))
												.queue();
										return interactionHook.editOriginal(R.Strings.ui("the_entry_change_was_successfully_requested"));
									} else
										return interactionHook.editOriginal(R.Strings.ui("the_entry_could_not_be_updated"));
								} else
									return interactionHook.editOriginal(R.Strings.ui("the_user_requesting_a_change_could_not_be_found"));
							} else
								return interactionHook.editOriginal(R.Strings.ui("the_request_channel_could_not_be_found"));
						} else return interactionHook.editOriginal(R.Strings.ui("the_guild_could_not_be_found"));
					} else if (!groupRepository.update(group).equals(Status.SUCCESS)) {
						return interactionHook.editOriginal(R.Strings.ui("the_entry_could_not_be_updated"));
					} else return interactionHook.editOriginal(R.Strings.ui("the_entry_was_successfully_updated"))
							.setEmbeds(GroupEmbed.of(group, userRepository, hnsUserRepository, usernameRepository));
				} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
			} else return interactionHook.editOriginal(R.Strings.ui("this_group_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
