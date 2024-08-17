package org.mintdaniel42.starediscordbot.data.repository;

import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.SpotEntityMeta;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

public final class SpotRepository {
	@NonNull private final Entityql entityQl;
	@NonNull private final SpotEntityMeta spotMeta;

	public SpotRepository(@NonNull final Config config) {
		entityQl = new Entityql(config);
		spotMeta = new SpotEntityMeta();
	}
}
