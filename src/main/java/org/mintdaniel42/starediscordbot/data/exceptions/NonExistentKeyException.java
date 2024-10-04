package org.mintdaniel42.starediscordbot.data.exceptions;

import org.mintdaniel42.starediscordbot.exception.BotException;

public class NonExistentKeyException extends BotException {
	public NonExistentKeyException() {
		super("we_could_not_find_such_an_entry");
	}
}
