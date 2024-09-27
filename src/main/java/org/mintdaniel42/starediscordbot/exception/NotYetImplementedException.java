package org.mintdaniel42.starediscordbot.exception;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.PropertyKey;

@Getter
public class NotYetImplementedException extends UnsupportedOperationException {
	@NonNull @PropertyKey(resourceBundle = "ui") protected final String message = "this_is_not_yet_implemented";
}
