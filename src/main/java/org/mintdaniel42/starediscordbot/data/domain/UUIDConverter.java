package org.mintdaniel42.starediscordbot.data.domain;

import lombok.NonNull;
import org.seasar.doma.ExternalDomain;
import org.seasar.doma.jdbc.domain.DomainConverter;

import java.util.UUID;

@ExternalDomain
public final class UUIDConverter implements DomainConverter<UUID, String> {

	@Override
	public String fromDomainToValue(@NonNull final UUID uuid) {
		return uuid.toString();
	}

	@Override
	public UUID fromValueToDomain(@NonNull final String value) {
		return UUID.fromString(value);
	}
}
