package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.MetaDataEntity;
import org.mintdaniel42.starediscordbot.data.entity.MetaDataEntityMeta;
import org.seasar.doma.jdbc.criteria.Entityql;

@Singleton
public final class MetaDataRepository extends BaseRepository<Integer, MetaDataEntity> {
	public MetaDataRepository(@NonNull final Entityql entityQl) {
		final var meta = new MetaDataEntityMeta();
		super(entityQl, meta, meta.id);
	}

	public @NonNull MetaDataEntity selectFirst() {
		try {
			return entityQl.from(meta)
					.fetchOptional()
					.orElse(new MetaDataEntity(0, MetaDataEntity.Version.UNKNOWN));
		} catch (Exception e) {
			return new MetaDataEntity(0, MetaDataEntity.Version.UNKNOWN);
		}
	}

	public void upsert(@NonNull final MetaDataEntity metaData) {
		if (selectById(metaData.id()).isPresent()) {
			entityQl.update(meta, metaData);
		} else entityQl.insert(meta, metaData);
	}
}
