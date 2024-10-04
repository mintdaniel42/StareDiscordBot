package org.mintdaniel42.starediscordbot.compose.exception;

import org.mintdaniel42.starediscordbot.exception.BotException;

public class CommandIncompleteException extends BotException {
	public CommandIncompleteException() {
		super("your_command_was_incomplete");
	}
}
