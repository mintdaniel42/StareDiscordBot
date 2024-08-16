package org.mintdaniel42.starediscordbot.data.repository;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.data.entity.AchievementEntity;
import org.mintdaniel42.starediscordbot.data.entity.AchievementEntityMeta;
import org.mintdaniel42.starediscordbot.utils.Status;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;

public final class AchievementRepository {
	@NonNull private final Entityql entityQl;
	@NonNull private final AchievementEntityMeta achievementMeta;

	public AchievementRepository(@NonNull final Config config) {
		entityQl = new Entityql(config);
		achievementMeta = new AchievementEntityMeta();
	}

	public @NonNull List<AchievementEntity> selectByTypeAndPoints(@Nullable final AchievementEntity.Type type, final int points) {
		if (type != null && points >= 0) {
			return entityQl.from(achievementMeta)
					.where(w -> {
						w.eq(achievementMeta.type, type);
						w.and(() -> w.eq(achievementMeta.points, points));
					})
					.fetch();
		} else if (type != null) {
			return selectByType(type);
		} else if (points >= 0) {
			return selectByPoints(points);
		} else return selectAll();
	}

	public @NonNull List<AchievementEntity> selectByType(@NonNull final AchievementEntity.Type type) {
		return entityQl.from(achievementMeta)
				.where(w -> w.eq(achievementMeta.type, type))
				.fetch();
	}

	public @NonNull List<AchievementEntity> selectByPoints(final int points) {
		return entityQl.from(achievementMeta)
				.where(w -> w.eq(achievementMeta.points, points))
				.fetch();
	}

	public @NonNull List<AchievementEntity> selectAll() {
		return entityQl.from(achievementMeta)
				.fetch();
	}

	public @NonNull Status insert(@NonNull final AchievementEntity achievement) {
		if (entityQl.from(achievementMeta)
				.where(w -> w.eq(achievementMeta.uuid, achievement.getUuid()))
				.fetchOptional()
				.isPresent()) return Status.DUPLICATE;
		return entityQl.insert(achievementMeta, achievement)
				.execute()
				.getCount() == 1 ? Status.SUCCESS : Status.ERROR;
	}
}
