package org.mintdaniel42.starediscordbot.compose.exception;

public class NoSuchChannelException extends ComposeException {
	public NoSuchChannelException() {
		super("a_channel_could_not_be_found");
	}
}
