package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.data.entity.SpotEntity;
import org.mintdaniel42.starediscordbot.data.entity.SpotEntityMeta;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.Optional;
import java.util.UUID;

@Singleton
public final class SpotRepository extends BaseRepository<UUID, SpotEntity> {
	public SpotRepository(@NonNull final Entityql entityQl) {
		final var meta = new SpotEntityMeta();
		super(entityQl, meta, meta.uuid);
	}

	public @NonNull Optional<SpotEntity> selectBy(@NonNull final UUID mapUUID, @NonNull final SpotEntity.Type type, final boolean twoPlayer, @Nullable final UUID finderUUID, @Nullable final String blockId, @Nullable final String rating) {
		return entityQl.from(meta)
				.where(w -> {
					w.eq(((SpotEntityMeta) meta).mapUUID, mapUUID);
					w.eq(((SpotEntityMeta) meta).type, type);
					w.eq(((SpotEntityMeta) meta).twoPlayer, twoPlayer);
					w.eq(((SpotEntityMeta) meta).finderUUID, finderUUID);
					w.eq(((SpotEntityMeta) meta).blockId, blockId);
					w.eq(((SpotEntityMeta) meta).rating, rating);
				})
				.fetchOptional();
	}
}
