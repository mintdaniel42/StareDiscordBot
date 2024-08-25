package org.mintdaniel42.starediscordbot.commands.pg;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.entity.PGUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.UserEntity;
import org.mintdaniel42.starediscordbot.data.repository.PGUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.embeds.user.pg.PGUserEmbed;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
@Singleton
public final class PGAddCommand implements CommandAdapter {
	@NonNull private final PGUserRepository pgUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final UsernameRepository usernameRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof final OptionMapping usernameMapping && event.getOptions().size() >= 2) {
			if (MCHelper.getUuid(usernameRepository, usernameMapping.getAsString()) instanceof final UUID uuid) {
				final var pgUser = PGUserEntity.merge(event.getOptions(), PGUserEntity.builder().uuid(uuid));
				final var userOptional = userRepository.selectById(uuid);
				final var user = userOptional.orElseGet(() -> UserEntity.builder()
						.uuid(uuid)
						.build());
				final var usernameOptional = usernameRepository.selectById(uuid);

				return usernameOptional.map(username -> interactionHook.editOriginal(switch (userRepository.insert(user)) {
							case ERROR -> R.Strings.ui("the_entry_could_not_be_created");
							case SUCCESS, DUPLICATE -> switch (pgUserRepository.insert(pgUser)) {
								case SUCCESS -> R.Strings.ui("the_entry_was_successfully_created");
								case DUPLICATE -> R.Strings.ui("this_user_entry_already_exists");
								case ERROR -> R.Strings.ui("the_entry_could_not_be_created");
							};
						}).setEmbeds(PGUserEmbed.of(pgUser, username, false)))
						.orElseGet(() -> interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist")));
			} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
