package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.SpotEntity;
import org.mintdaniel42.starediscordbot.data.entity.SpotEntityMeta;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.UUID;

@Singleton
public final class SpotRepository extends BaseRepository<UUID, SpotEntity> {
	public SpotRepository(@NonNull final Config config) {
		final var meta = new SpotEntityMeta();
		super(new Entityql(config), meta, meta.uuid);
	}
}
