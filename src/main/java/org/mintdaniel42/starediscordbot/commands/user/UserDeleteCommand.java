package org.mintdaniel42.starediscordbot.commands.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.UserModel;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
public class UserDeleteCommand implements CommandAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull InteractionHook interactionHook, @NonNull SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof OptionMapping usernameMapping) {
			if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof UUID uuid) {
				if (databaseAdapter.getUser(uuid) instanceof UserModel userModel) {
					if (databaseAdapter.deleteUser(uuid)) {
						return interactionHook.editOriginal(R.Strings.ui("the_user_s_was_successfully_deleted",
								userModel.getUsername()));
					} else return interactionHook.editOriginal(R.Strings.ui("the_user_s_could_not_be_deleted",
							userModel.getUsername()));
				} else return interactionHook.editOriginal(R.Strings.ui("this_user_entry_does_not_exist"));
			} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
