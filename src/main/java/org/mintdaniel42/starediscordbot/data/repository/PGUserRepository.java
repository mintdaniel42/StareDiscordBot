package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.entity.PGUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.PGUserEntityMeta;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;
import java.util.UUID;

@Singleton
public final class PGUserRepository extends BaseRepository<UUID, PGUserEntity> {
	public PGUserRepository(@NonNull final Entityql entityQl) {
		final var meta = new PGUserEntityMeta();
		super(entityQl, meta, meta.uuid);
	}

	public @NonNull List<PGUserEntity> selectByPage(final int page) {
		return entityQl.from(meta)
				.orderBy(o -> o.desc(((PGUserEntityMeta) meta).points))
				.stream()
				.skip((long) BuildConfig.entriesPerPage * page)
				.limit(BuildConfig.entriesPerPage)
				.toList();
	}
}
