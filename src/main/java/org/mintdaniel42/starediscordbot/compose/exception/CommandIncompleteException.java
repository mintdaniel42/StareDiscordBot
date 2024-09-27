package org.mintdaniel42.starediscordbot.compose.exception;

public class CommandIncompleteException extends ComposeException {
	public CommandIncompleteException() {
		super("your_command_was_incomplete");
	}
}
