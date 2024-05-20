package org.mintdaniel42.starediscordbot;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class OrExtension {
	public <T> @NonNull T or(@Nullable final T object, @NonNull final T fallback) {
		return object != null ? object : fallback;
	}
}
