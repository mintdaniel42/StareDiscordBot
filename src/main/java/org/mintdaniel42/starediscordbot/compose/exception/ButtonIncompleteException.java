package org.mintdaniel42.starediscordbot.compose.exception;

import org.mintdaniel42.starediscordbot.exception.BotException;

public class ButtonIncompleteException extends BotException {
	public ButtonIncompleteException() {
		super("this_button_seems_to_be_faulty");
	}
}
