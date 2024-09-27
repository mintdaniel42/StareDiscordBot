package org.mintdaniel42.starediscordbot.compose.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.PropertyKey;

@Getter
@RequiredArgsConstructor
public class ComposeException extends Exception {
	@NonNull @PropertyKey(resourceBundle = "ui") protected final String message;
}
