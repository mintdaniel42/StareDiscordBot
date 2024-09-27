package org.mintdaniel42.starediscordbot.compose.exception;

public class NoSuchEntryException extends ComposeException {
	public NoSuchEntryException() {
		super("we_could_not_find_such_an_entry");
	}
}
