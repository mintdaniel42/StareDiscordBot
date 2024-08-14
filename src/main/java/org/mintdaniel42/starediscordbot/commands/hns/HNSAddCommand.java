package org.mintdaniel42.starediscordbot.commands.hns;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.HNSUserModel;
import org.mintdaniel42.starediscordbot.data.UserModel;
import org.mintdaniel42.starediscordbot.embeds.UserEmbed;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
public final class HNSAddCommand implements CommandAdapter {
	@NonNull private final DatabaseAdapter databaseAdapter;

	@Override
	public @NonNull WebhookMessageEditAction<Message> handle(@NonNull final InteractionHook interactionHook, @NonNull final SlashCommandInteractionEvent event) {
		if (event.getOption("username") instanceof final OptionMapping usernameMapping && event.getOptions().size() >= 2) {
			if (MCHelper.getUuid(databaseAdapter, usernameMapping.getAsString()) instanceof final UUID uuid) {
				final var hnsModel = HNSUserModel.merge(event.getOptions(), HNSUserModel.builder().uuid(uuid));
				final var userModel = databaseAdapter.getUser(uuid);
				final var userBuilder = userModel == null ? UserModel.builder().uuid(uuid) : userModel.toBuilder();
				userBuilder.hnsUser(hnsModel)
						.username(MCHelper.getUsername(uuid));

				return interactionHook.editOriginal(switch (databaseAdapter.addUser(userBuilder.build())) {
					case ERROR -> R.Strings.ui("the_entry_could_not_be_created");
					case SUCCESS, DUPLICATE -> switch (databaseAdapter.addHnsUser(hnsModel)) {
						case SUCCESS -> R.Strings.ui("the_entry_was_successfully_created");
						case DUPLICATE -> R.Strings.ui("this_user_entry_already_exists");
						case ERROR -> R.Strings.ui("the_entry_could_not_be_created");
					};
				}).setEmbeds(UserEmbed.of(userBuilder.build(), UserEmbed.Type.HNS_ALL));
			} else return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		} else return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
	}
}
