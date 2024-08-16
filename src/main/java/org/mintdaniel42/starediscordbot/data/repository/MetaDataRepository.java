package org.mintdaniel42.starediscordbot.data.repository;

import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.MetaDataEntity;
import org.mintdaniel42.starediscordbot.data.entity.MetaDataEntityMeta;
import org.mintdaniel42.starediscordbot.utils.Status;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

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

	public Status update(@NonNull final MetaDataEntity metaData) {
		return entityQl.update(metaDataMeta, metaData)
				.execute()
				.getCount() == 1 ? Status.SUCCESS : Status.ERROR;
	}
}
