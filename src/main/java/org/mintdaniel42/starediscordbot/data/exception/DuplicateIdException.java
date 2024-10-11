package org.mintdaniel42.starediscordbot.data.exception;

import org.mintdaniel42.starediscordbot.exception.BotException;

public class DuplicateIdException extends BotException {
	public DuplicateIdException() {
		super("this_entry_already_exists");
	}
}
