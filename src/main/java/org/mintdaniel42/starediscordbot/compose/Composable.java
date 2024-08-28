package org.mintdaniel42.starediscordbot.compose;

import lombok.NonNull;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.mintdaniel42.starediscordbot.compose.exceptions.NoSuchEntryException;
import org.mintdaniel42.starediscordbot.data.repository.BaseRepository;

import java.util.function.Function;

public interface Composable<CONTEXT> {
	@NonNull
	MessageEditData compose(@NonNull final CONTEXT context);

	/* ========== ENTITIES ========== */
	default @NonNull <ID, ENTITY> ENTITY requireEntity(@NonNull final BaseRepository<ID, ENTITY> repository, ID id) {
		return repository.selectById(id)
				.orElseThrow(NoSuchEntryException::new);
	}

	default @NonNull <ID, ENTITY, T> T requireEntity(@NonNull final BaseRepository<ID, ENTITY> repository, ID id, Function<ENTITY, T> function) {
		return repository.selectById(id)
				.map(function)
				.orElseThrow(NoSuchEntryException::new);
	}
}
