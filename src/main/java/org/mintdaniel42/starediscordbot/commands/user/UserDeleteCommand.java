package org.mintdaniel42.starediscordbot.commands.user;

import jakarta.inject.Singleton;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.Database;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.R;
import org.mintdaniel42.starediscordbot.utils.Status;

import java.util.UUID;

@RequiredArgsConstructor
@Singleton
public final class UserDeleteCommand implements CommandAdapter {
	@NonNull private final Database database;
	@NonNull private final UsernameRepository usernameRepository;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof OptionMapping usernameMapping) {
			if (MCHelper.getUuid(usernameRepository, usernameMapping.getAsString()) instanceof UUID uuid) {
				final var usernameOptional = usernameRepository.selectById(uuid);
				if (usernameOptional.isPresent() && database.deleteUserData(uuid).equals(Status.SUCCESS)) {
					return interactionHook.editOriginal(R.Strings.ui("the_user_s_was_successfully_deleted",
							usernameOptional.get().getUsername()));
				} else return interactionHook.editOriginal(R.Strings.ui("the_user_s_could_not_be_deleted",
						usernameMapping.getAsString()));
			} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
