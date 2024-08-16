package org.mintdaniel42.starediscordbot.data.repository;

import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.RequestEntity;
import org.mintdaniel42.starediscordbot.data.entity.RequestEntityMeta;
import org.mintdaniel42.starediscordbot.utils.Status;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;
import java.util.Optional;

public final class RequestRepository {
	@NonNull private final Entityql entityQl;
	@NonNull private final RequestEntityMeta requestMeta;

	public RequestRepository(@NonNull final Config config) {
		entityQl = new Entityql(config);
		requestMeta = new RequestEntityMeta();
	}

	public @NonNull List<RequestEntity> selectAll() {
		return entityQl.from(requestMeta).fetch();
	}

	public @NonNull Optional<RequestEntity> selectById(final long id) {
		return Optional.empty();
	}

	public @NonNull Status insert(@NonNull final RequestEntity request) {
		return Status.ERROR;
	}

	public @NonNull Status deleteById(final long id) {
		return selectById(id).filter(request -> entityQl.delete(requestMeta, request)
						.execute()
						.getCount() == 1)
				.map(_ -> Status.SUCCESS)
				.orElse(Status.ERROR);
	}
}
