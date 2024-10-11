package org.mintdaniel42.starediscordbot.compose.exception;

import org.mintdaniel42.starediscordbot.exception.BotException;

public class NoSuchChannelException extends BotException {
	public NoSuchChannelException() {
		super("a_channel_could_not_be_found");
	}
}
