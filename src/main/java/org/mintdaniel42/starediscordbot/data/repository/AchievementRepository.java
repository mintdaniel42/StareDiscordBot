package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.data.entity.AchievementEntity;
import org.mintdaniel42.starediscordbot.data.entity.AchievementEntityMeta;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;
import java.util.UUID;

@Singleton
public final class AchievementRepository extends BaseRepository<UUID, AchievementEntity> {
	public AchievementRepository(@NonNull final Config config) {
		final var meta = new AchievementEntityMeta();
		super(new Entityql(config), meta, meta.uuid);
	}

	public @NonNull List<AchievementEntity> selectByTypeAndPoints(@Nullable final AchievementEntity.Type type, final int points) {
		if (type != null && points >= 0) {
			return entityQl.from(meta)
					.where(w -> {
						w.eq(((AchievementEntityMeta) meta).type, type);
						w.and(() -> w.eq(((AchievementEntityMeta) meta).points, points));
					})
					.fetch();
		} else if (type != null) {
			return selectByType(type);
		} else if (points >= 0) {
			return selectByPoints(points);
		} else return selectAll();
	}

	public @NonNull List<AchievementEntity> selectByType(@NonNull final AchievementEntity.Type type) {
		return entityQl.from(meta)
				.where(w -> w.eq(((AchievementEntityMeta) meta).type, type))
				.fetch();
	}

	public @NonNull List<AchievementEntity> selectByPoints(final int points) {
		return entityQl.from(meta)
				.where(w -> w.eq(((AchievementEntityMeta) meta).points, points))
				.fetch();
	}
}
