package org.mintdaniel42.starediscordbot.commands.hns.maps;

import jakarta.inject.Singleton;
import lombok.NonNull;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.aspect.annotation.NotYetImplemented;
import org.mintdaniel42.starediscordbot.compose.command.BaseComposeCommand;
import org.mintdaniel42.starediscordbot.compose.command.CommandContext;
import org.mintdaniel42.starediscordbot.exception.BotException;

@Singleton
public class MapsShowCommand extends BaseComposeCommand {
	@Override
	@NotYetImplemented
	protected @NonNull MessageEditData compose(@NonNull final CommandContext context) throws BotException {
		return null;
	}

	@Override
	public @NonNull String getCommandId() {
		return "hns maps show";
	}
}
