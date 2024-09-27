package org.mintdaniel42.starediscordbot.compose.exception;

import org.mintdaniel42.starediscordbot.exception.BotException;

public class NoSuchPageException extends BotException {
	public NoSuchPageException() {
		super("this_page_does_not_exist");
	}
}
