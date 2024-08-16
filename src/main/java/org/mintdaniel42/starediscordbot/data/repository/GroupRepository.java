package org.mintdaniel42.starediscordbot.data.repository;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.data.entity.GroupEntity;
import org.mintdaniel42.starediscordbot.data.entity.GroupEntityMeta;
import org.mintdaniel42.starediscordbot.utils.Status;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;
import java.util.Optional;

public final class GroupRepository {
	@NonNull private final Entityql entityQl;
	@NonNull private final GroupEntityMeta groupMeta;

	public GroupRepository(@NonNull final Config config) {
		entityQl = new Entityql(config);
		groupMeta = new GroupEntityMeta();
	}

	public @NonNull Optional<GroupEntity> selectByTag(@Nullable final String tag) {
		return entityQl.from(groupMeta)
				.where(w -> w.eq(groupMeta.tag, tag))
				.fetchOptional();
	}

	public @NonNull List<GroupEntity> selectByTagLike(@NonNull final String like) {
		return entityQl.from(groupMeta)
				.where(w -> w.like(groupMeta.name, "%%%s%%".formatted(like)))
				.fetch();
	}

	public @NonNull Status insert(@NonNull final GroupEntity group) {
		if (entityQl.from(groupMeta)
				.where(w -> w.eq(groupMeta.tag, group.getTag()))
				.fetchOptional()
				.isPresent()) return Status.DUPLICATE;
		return entityQl.insert(groupMeta, group)
				.execute()
				.getCount() == 1 ? Status.SUCCESS : Status.ERROR;
	}

	public @NonNull Status update(@NonNull final GroupEntity group) {
		return entityQl.update(groupMeta, group)
				.execute()
				.getCount() == 1 ? Status.SUCCESS : Status.ERROR;
	}

	public @NonNull Status deleteByTag(@NonNull final String tag) {
		return selectByTag(tag).filter(group -> entityQl.delete(groupMeta, group)
						.execute()
						.getCount() == 1)
				.map(_ -> Status.SUCCESS)
				.orElse(Status.ERROR);
	}
}