package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.UserEntity;
import org.mintdaniel42.starediscordbot.data.entity.UserEntityMeta;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;
import java.util.UUID;

@Singleton
public final class UserRepository extends BaseRepository<UUID, UserEntity> {
	public UserRepository(@NonNull final Config config) {
		final var meta = new UserEntityMeta();
		super(new Entityql(config), meta, meta.uuid);
	}

	public @NonNull List<UserEntity> selectByGroupTag(@NonNull final String groupTag) {
		return entityQl.from(meta)
				.where(w -> w.eq(((UserEntityMeta) meta).groupTag, groupTag))
				.fetch();
	}
}
