package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntityMeta;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;
import java.util.UUID;

@Singleton
public final class HNSUserRepository extends BaseRepository<UUID, HNSUserEntity> {
	public HNSUserRepository(@NonNull final Entityql entityQl) {
		final var meta = new HNSUserEntityMeta();
		super(entityQl, meta, meta.uuid);
	}

	@Override
	public @NonNull List<HNSUserEntity> selectAll() {
		return entityQl.from(meta)
				.orderBy(o -> o.desc(((HNSUserEntityMeta) meta).points))
				.fetch();
	}
}
