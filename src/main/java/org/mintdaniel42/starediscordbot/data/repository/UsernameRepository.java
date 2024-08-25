package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.UsernameEntity;
import org.mintdaniel42.starediscordbot.data.entity.UsernameEntityMeta;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public final class UsernameRepository extends BaseRepository<UUID, UsernameEntity> {
	public UsernameRepository(@NonNull final Config config) {
		final var meta = new UsernameEntityMeta();
		super(new Entityql(config), meta, meta.uuid);
	}

	public @NonNull List<UsernameEntity> selectByUsernameLike(@NonNull final String like) {
		return entityQl.from(meta)
				.where(w -> w.like(((UsernameEntityMeta) meta).username, "%%%s%%".formatted(like)))
				.fetch();
	}

	public @NonNull Optional<UsernameEntity> selectByUsername(@NonNull final String username) {
		return entityQl.from(meta)
				.where(w -> w.eq(((UsernameEntityMeta) meta).username, username))
				.fetchOptional();
	}
}
