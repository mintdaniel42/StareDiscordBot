package org.mintdaniel42.starediscordbot.compose;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mintdaniel42.starediscordbot.compose.exceptions.UnknownUsernameException;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;
import org.mintdaniel42.starediscordbot.utils.MCHelper;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor
public abstract class ComposerHelper {
	@NonNull private final UsernameRepository usernameRepository;

	/* ========== UUID ========== */
	protected final @NonNull UUID requireUUID(@NonNull final String username) {
		return requireUUID(username, uuid -> uuid);
	}

	protected final @NonNull <T> T requireUUID(@NonNull final String username, @NonNull final Function<UUID, T> function) {
		return Optional.ofNullable(MCHelper.getUuid(usernameRepository, username))
				.map(function)
				.orElseThrow(UnknownUsernameException::new);
	}
}
