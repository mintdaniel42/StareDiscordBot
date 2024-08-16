package org.mintdaniel42.starediscordbot.data.repository;

import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.MapEntityMeta;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

public final class MapRepository {
	@NonNull private final Entityql entityQl;
	@NonNull private final MapEntityMeta mapMeta;

	public MapRepository(@NonNull final Config config) {
		entityQl = new Entityql(config);
		mapMeta = new MapEntityMeta();
	}
}
