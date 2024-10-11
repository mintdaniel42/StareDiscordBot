package org.mintdaniel42.starediscordbot.data.exception;

import org.mintdaniel42.starediscordbot.exception.BotException;

public class EntryUpdateFailedException extends BotException {
	public EntryUpdateFailedException() {
		super("the_entry_could_not_be_updated");
	}
}
