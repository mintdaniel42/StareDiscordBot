package org.mintdaniel42.starediscordbot.data.domain;

import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import java.util.UUID;

@ExternalDomain
public final class UUIDConverter implements DomainConverter<UUID, String> {

	@Contract(value = "null -> null", pure = true)
	@Override
	public @Nullable String fromDomainToValue(@Nullable final UUID uuid) {
		if (uuid == null) return null;
		return uuid.toString();
	}

	@Contract(value = "null -> null; !null -> !null", pure = true)
	@Override
	public @Nullable UUID fromValueToDomain(@Nullable final String value) {
		if (value == null) return null;
		return UUID.fromString(value);
	}
}
