package org.mintdaniel42.starediscordbot.di;

import io.avaje.inject.BeanScope;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class DI {
	@NonNull private final BeanScope beanScope = BeanScope.builder()
			.shutdownHook(true)
			.build();

	public @NonNull <T> T get(@NonNull final Class<T> tClass) {
		return beanScope.get(tClass);
	}

	public @NonNull <T> List<T> list(@NonNull final Class<T> tClass) {
		return beanScope.list(tClass);
	}
}
