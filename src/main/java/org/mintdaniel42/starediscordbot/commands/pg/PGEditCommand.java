package org.mintdaniel42.starediscordbot.commands.pg;

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
import org.mintdaniel42.starediscordbot.data.entity.PGUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.RequestEntity;
import org.mintdaniel42.starediscordbot.data.repository.PGUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.RequestRepository;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.embeds.user.pg.PGUserEmbed;
import org.mintdaniel42.starediscordbot.utils.*;

import java.util.UUID;

@RequiredArgsConstructor
public final class PGEditCommand implements CommandAdapter {
	@NonNull private final PGUserRepository pgUserRepository;
	@NonNull private final RequestRepository requestRepository;
	@NonNull private final UsernameRepository usernameRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof final OptionMapping usernameMapping &&
				event.getOptions().size() >= 2) {
			if (MCHelper.getUuid(usernameRepository, usernameMapping.getAsString()) instanceof final UUID uuid) {
				final var pgUserOptional = pgUserRepository.selectByUUID(uuid);
				final var usernameOptional = usernameRepository.selectByUUID(uuid);
				if (pgUserOptional.isPresent() && usernameOptional.isPresent()) {
					final var pgUser = PGUserEntity.merge(event.getOptions(), pgUserOptional.get().toBuilder());

					if (!DCHelper.hasRole(event.getMember(), Options.getEditRoleId()) && !DCHelper.hasRole(event.getMember(), Options.getCreateRoleId())) {
						long timestamp = System.currentTimeMillis();
						if (event.getGuild() instanceof Guild guild) {
							if (guild.getTextChannelById(Options.getRequestChannelId()) instanceof TextChannel requestChannel) {
								if (event.getMember() instanceof Member member) {
									if (requestRepository.insert(RequestEntity.from(timestamp, pgUser)).equals(Status.SUCCESS)) {
										requestChannel.sendMessage(R.Strings.ui("the_user_s_requested_an_edit_you_can_approve_it_with_approve_s",
														member.getAsMention(),
														timestamp))
												.setActionRow(ApproveButton.create(timestamp))
												.addEmbeds(PGUserEmbed.of(pgUser, usernameOptional.get(), true))
												.queue();
										return interactionHook.editOriginal(R.Strings.ui("the_entry_change_was_successfully_requested"));
									} else
										return interactionHook.editOriginal(R.Strings.ui("the_entry_could_not_be_updated"));
								} else
									return interactionHook.editOriginal(R.Strings.ui("the_user_requesting_a_change_could_not_be_found"));
							} else
								return interactionHook.editOriginal(R.Strings.ui("the_request_channel_could_not_be_found"));
						} else return interactionHook.editOriginal(R.Strings.ui("the_guild_could_not_be_found"));
					} else if (!pgUserRepository.update(pgUser).equals(Status.SUCCESS)) {
						return interactionHook.editOriginal(R.Strings.ui("the_entry_could_not_be_updated"));
					} else return interactionHook.editOriginal(R.Strings.ui("the_entry_was_successfully_updated"))
							.setEmbeds(PGUserEmbed.of(pgUser, usernameOptional.get(), false));
				} else return interactionHook.editOriginal(R.Strings.ui("this_user_entry_does_not_exist"));
			} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
