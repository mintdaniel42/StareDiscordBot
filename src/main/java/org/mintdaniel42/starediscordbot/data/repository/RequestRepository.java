package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.RequestEntity;
import org.mintdaniel42.starediscordbot.data.entity.RequestEntityMeta;
import org.seasar.doma.jdbc.criteria.Entityql;

@Singleton
public final class RequestRepository extends BaseRepository<Long, RequestEntity> {
	public RequestRepository(@NonNull final Entityql entityQl) {
		final var meta = new RequestEntityMeta();
		super(entityQl, meta, meta.timestamp);
	}
}
