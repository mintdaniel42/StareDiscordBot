package org.mintdaniel42.starediscordbot.compose.exception;

import org.mintdaniel42.starediscordbot.exception.BotException;

public class NoSuchEntryException extends BotException {
	public NoSuchEntryException() {
		super("we_could_not_find_such_an_entry");
	}
}
