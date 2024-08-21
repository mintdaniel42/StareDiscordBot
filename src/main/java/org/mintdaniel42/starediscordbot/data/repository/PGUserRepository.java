package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.entity.PGUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.PGUserEntityMeta;
import org.mintdaniel42.starediscordbot.utils.Status;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public final class PGUserRepository {
	@NonNull private final Entityql entityQl;
	@NonNull private final PGUserEntityMeta pgUserMeta;

	public PGUserRepository(@NonNull final Config config) {
		entityQl = new Entityql(config);
		pgUserMeta = new PGUserEntityMeta();
	}

	public @NonNull List<PGUserEntity> selectByPage(final int page) {
		return entityQl.from(pgUserMeta)
				.orderBy(o -> o.desc(pgUserMeta.points))
				.stream()
				.skip((long) BuildConfig.entriesPerPage * page)
				.limit(BuildConfig.entriesPerPage)
				.toList();
	}

	public @NonNull Optional<PGUserEntity> selectByUUID(@NonNull final UUID uuid) {
		return entityQl.from(pgUserMeta)
				.where(c -> c.eq(pgUserMeta.uuid, uuid))
				.fetchOptional();
	}

	public @NonNull Status insert(@NonNull final PGUserEntity pgUser) {
		if (entityQl.from(pgUserMeta)
				.where(w -> w.eq(pgUserMeta.uuid, pgUser.getUuid()))
				.fetchOptional()
				.isPresent()) return Status.DUPLICATE;
		return entityQl.insert(pgUserMeta, pgUser)
				.execute()
				.getCount() == 1 ? Status.SUCCESS : Status.ERROR;
	}

	public @NonNull Status update(@NonNull final PGUserEntity pgUser) {
		return entityQl.update(pgUserMeta, pgUser)
				.execute()
				.getCount() == 1 ? Status.SUCCESS : Status.ERROR;
	}

	public @NonNull Status deleteByUUID(@NonNull final UUID uuid) {
		return selectByUUID(uuid).filter(pgUser -> entityQl.delete(pgUserMeta, pgUser)
						.execute()
						.getCount() == 1)
				.map(_ -> Status.SUCCESS)
				.orElse(Status.ERROR);
	}

	public int countPages() {
		return (int) Math.ceil((double) entityQl.from(pgUserMeta)
				.fetch()
				.size() / BuildConfig.entriesPerPage);
	}
}
