package org.mintdaniel42.starediscordbot.data.repository;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.exceptions.DuplicateIdException;
import org.mintdaniel42.starediscordbot.data.exceptions.NonExistentKeyException;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.seasar.doma.jdbc.JdbcException;
import org.seasar.doma.jdbc.criteria.Entityql;
import org.seasar.doma.jdbc.criteria.metamodel.EntityMetamodel;
import org.seasar.doma.jdbc.criteria.metamodel.PropertyMetamodel;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public abstract class BaseRepository<ID, ENTITY> {
	@NonNull protected final Entityql entityQl;
	@NonNull protected final EntityMetamodel<ENTITY> meta;
	@NonNull protected final PropertyMetamodel<ID> idProperty;

	public @NonNull List<ENTITY> selectAll() {
		return entityQl.from(meta).fetch();
	}

	public @NonNull List<ENTITY> selectAll(final int offset, final int limit) {
		return selectAll()
				.stream()
				.skip(offset)
				.limit(limit)
				.toList();
	}

	public @NonNull Optional<ENTITY> selectById(@NonNull final ID id) {
		return entityQl.from(meta)
				.where(c -> c.eq(idProperty, id))
				.fetchOptional();
	}

	public int count() {
		return entityQl.from(meta)
				.fetch()
				.size();
	}

	public boolean has(@NonNull final ID id) {
		return selectById(id).isPresent();
	}

	public void insert(@NonNull final ENTITY entity) throws BotException {
		try {
			if (entityQl.insert(meta, entity)
					.execute()
					.getCount() != 1) throw new DuplicateIdException();
		} catch (JdbcException _) {
			throw new BotException("the_entry_could_not_be_created");
		}
	}

	public void update(@NonNull final ENTITY entity) throws BotException {
		if (entityQl.update(meta, entity)
				.execute()
				.getCount() != 1) throw new BotException("the_entry_could_not_be_updated");
	}

	public void deleteById(@NonNull final ID id) throws BotException {
		selectById(id)
				.map(entity -> entityQl.delete(meta, entity)
						.execute()
						.getCount())
				.orElseThrow(NonExistentKeyException::new);
	}
}
