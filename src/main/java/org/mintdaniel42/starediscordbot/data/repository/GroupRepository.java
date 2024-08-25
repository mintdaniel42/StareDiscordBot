package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.GroupEntity;
import org.mintdaniel42.starediscordbot.data.entity.GroupEntityMeta;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;

@Singleton
public final class GroupRepository extends BaseRepository<String, GroupEntity> {
	public GroupRepository(@NonNull final Entityql entityQl) {
		final var meta = new GroupEntityMeta();
		super(entityQl, meta, meta.tag);
	}

	public @NonNull List<GroupEntity> selectByTagLike(@NonNull final String like) {
		return entityQl.from(meta)
				.where(w -> w.like(((GroupEntityMeta) meta).name, "%%%s%%".formatted(like)))
				.fetch();
	}
}