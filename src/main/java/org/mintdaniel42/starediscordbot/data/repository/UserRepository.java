package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.UserEntity;
import org.mintdaniel42.starediscordbot.data.entity.UserEntityMeta;
import org.mintdaniel42.starediscordbot.data.exception.EntryUpdateFailedException;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.seasar.doma.jdbc.JdbcException;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;
import java.util.UUID;

@Singleton
public final class UserRepository extends BaseRepository<UUID, UserEntity> {
	public UserRepository(@NonNull final Entityql entityQl) {
		final var meta = new UserEntityMeta();
		super(entityQl, meta, meta.uuid);
	}

	public @NonNull List<UserEntity> selectByGroupTag(@NonNull final String groupTag) {
		return entityQl.from(meta)
				.where(w -> w.eq(((UserEntityMeta) meta).groupTag, groupTag))
				.fetch();
	}

	public void upsert(@NonNull final UserEntity user) throws BotException {
		try {
			if (selectById(user.getUuid()).isPresent()) {
				entityQl.update(meta, user);
			} else entityQl.insert(meta, user);
		} catch (JdbcException _) {
			throw new EntryUpdateFailedException();
		}
	}
}
