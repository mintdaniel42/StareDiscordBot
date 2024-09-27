package org.mintdaniel42.starediscordbot.compose.exception;

public class UnknownUsernameException extends ComposeException {
	public UnknownUsernameException() {
		super("this_username_does_not_exist");
	}
}
