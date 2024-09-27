package org.mintdaniel42.starediscordbot.compose.exception;

import org.mintdaniel42.starediscordbot.exception.BotException;

public class UnknownUsernameException extends BotException {
	public UnknownUsernameException() {
		super("this_username_does_not_exist");
	}
}
