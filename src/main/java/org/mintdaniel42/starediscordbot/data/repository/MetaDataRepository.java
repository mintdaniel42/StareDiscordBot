package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.MetaDataEntity;
import org.mintdaniel42.starediscordbot.data.entity.MetaDataEntityMeta;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

@Singleton
public final class MetaDataRepository {
	@NonNull private final Entityql entityQl;
	@NonNull private final MetaDataEntityMeta metaDataMeta;

	public MetaDataRepository(@NonNull final Config config) {
		entityQl = new Entityql(config);
		metaDataMeta = new MetaDataEntityMeta();
	}

	public MetaDataEntity selectFirst() {
		return entityQl.from(metaDataMeta)
				.fetchOne();
	}

	public void insertOrUpdate(@NonNull final MetaDataEntity metaData) {
		if (entityQl.from(metaDataMeta)
				.where(w -> w.eq(metaDataMeta.id, metaData.id()))
				.fetchOptional()
				.isPresent()) {
			entityQl.update(metaDataMeta, metaData)
					.execute();
		} else entityQl.insert(metaDataMeta, metaData).execute();
	}
}
