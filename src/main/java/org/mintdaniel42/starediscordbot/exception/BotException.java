package org.mintdaniel42.starediscordbot.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.PropertyKey;

@RequiredArgsConstructor
@Getter(onMethod_ = @PropertyKey(resourceBundle = "ui"))
public class BotException extends Exception {
	@NonNull @PropertyKey(resourceBundle = "ui") protected final String message;
}
