package org.mintdaniel42.starediscordbot.data.repository;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.utils.Status;
import org.seasar.doma.jdbc.JdbcException;
import org.seasar.doma.jdbc.criteria.Entityql;
import org.seasar.doma.jdbc.criteria.metamodel.EntityMetamodel;
import org.seasar.doma.jdbc.criteria.metamodel.PropertyMetamodel;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public abstract class BaseRepository<ID, ENTITY> {
	protected Entityql entityQl;
	protected EntityMetamodel<ENTITY> meta;
	protected PropertyMetamodel<ID> idProperty;

	public @NonNull List<ENTITY> selectAll() {
		return entityQl.from(meta).fetch();
	}

	public @NonNull Optional<ENTITY> selectById(@NonNull final ID id) {
		return entityQl.from(meta)
				.where(c -> c.eq(idProperty, id))
				.fetchOptional();
	}

	public @NonNull Status insert(@NonNull final ENTITY entity) {
		try {
			return entityQl.insert(meta, entity)
					.execute()
					.getCount() == 1 ? Status.SUCCESS : Status.DUPLICATE;
		} catch (JdbcException _) {
			return Status.ERROR;
		}
	}

	public @NonNull Status update(@NonNull final ENTITY entity) {
		return entityQl.update(meta, entity)
				.execute()
				.getCount() == 1 ? Status.SUCCESS : Status.ERROR;
	}

	public @NonNull Status deleteById(@NonNull final ID id) {
		return selectById(id).filter(hnsUser -> entityQl.delete(meta, hnsUser)
						.execute()
						.getCount() == 1)
				.map(_ -> Status.SUCCESS)
				.orElse(Status.ERROR);
	}

	public int count() {
		return entityQl.from(meta)
				.fetch()
				.size();
	}
}
