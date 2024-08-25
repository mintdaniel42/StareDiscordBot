package org.mintdaniel42.starediscordbot.commands.hns;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.UserEntity;
import org.mintdaniel42.starediscordbot.data.repository.HNSUserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UserRepository;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.embeds.user.hns.HNSFullUserEmbed;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
@Singleton
public final class HNSAddCommand implements CommandAdapter {
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final UserRepository userRepository;
	@NonNull private final UsernameRepository usernameRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof final OptionMapping usernameMapping && event.getOptions().size() >= 2) {
			if (MCHelper.getUuid(usernameRepository, usernameMapping.getAsString()) instanceof final UUID uuid) {
				final var hnsUser = HNSUserEntity.merge(event.getOptions(), HNSUserEntity.builder().uuid(uuid));
				final var userOptional = userRepository.selectById(uuid);
				final var user = userOptional.orElseGet(() -> UserEntity.builder()
						.uuid(uuid)
						.build());
				final var usernameOptional = usernameRepository.selectByUUID(uuid);

				return usernameOptional.map(username -> interactionHook.editOriginal(switch (userRepository.insert(user)) {
					case ERROR -> R.Strings.ui("the_entry_could_not_be_created");
							case SUCCESS, DUPLICATE -> switch (hnsUserRepository.insert(hnsUser)) {
						case SUCCESS -> R.Strings.ui("the_entry_was_successfully_created");
						case DUPLICATE -> R.Strings.ui("this_user_entry_already_exists");
						case ERROR -> R.Strings.ui("the_entry_could_not_be_created");
					};
						}).setEmbeds(HNSFullUserEmbed.of(hnsUser, username, false)))
						.orElseGet(() -> interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist")));
			} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
