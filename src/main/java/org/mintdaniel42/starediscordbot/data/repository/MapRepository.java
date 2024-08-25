package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.MapEntity;
import org.mintdaniel42.starediscordbot.data.entity.MapEntityMeta;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.UUID;

@Singleton
public final class MapRepository extends BaseRepository<UUID, MapEntity> {
	public MapRepository(@NonNull final Entityql entityQl) {
		final var meta = new MapEntityMeta();
		super(entityQl, meta, meta.uuid);
	}
}
