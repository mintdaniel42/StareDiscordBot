package org.mintdaniel42.starediscordbot.compose.exception;

public class NoSuchPageException extends ComposeException {
	public NoSuchPageException() {
		super("this_page_does_not_exist");
	}
}
