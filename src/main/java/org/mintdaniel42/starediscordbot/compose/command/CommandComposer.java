package org.mintdaniel42.starediscordbot.compose.command;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.commands.CommandAdapter;
import org.mintdaniel42.starediscordbot.compose.exceptions.CommandIncompleteException;
import org.mintdaniel42.starediscordbot.compose.exceptions.UnknownUsernameException;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.utils.R;

public abstract class CommandComposer extends CommandHelper implements CommandAdapter {
	public CommandComposer(@NonNull final UsernameRepository usernameRepository) {
		super(usernameRepository);
	}

	@Override
	public final @NonNull WebhookMessageEditAction<Message> handle(@NonNull InteractionHook interactionHook, @NonNull SlashCommandInteractionEvent event) {
		try {
			return interactionHook.editOriginal(compose(new CommandContext(event.getOptions())));
		} catch (IllegalArgumentException _) {
			return interactionHook.editOriginal(R.Strings.ui("one_of_your_options_was_invalid"));
		} catch (CommandIncompleteException _) {
			return interactionHook.editOriginal(R.Strings.ui("your_command_was_incomplete"));
		} catch (UnknownUsernameException _) {
			return interactionHook.editOriginal(R.Strings.ui("this_username_does_not_exist"));
		}
	}

	protected abstract @NonNull MessageEditData compose(@NonNull final CommandContext context);
}
