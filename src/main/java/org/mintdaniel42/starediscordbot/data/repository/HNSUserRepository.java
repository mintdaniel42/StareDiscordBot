package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntityMeta;
import org.mintdaniel42.starediscordbot.utils.Status;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public final class HNSUserRepository {
	@NonNull private final Entityql entityQl;
	@NonNull private final HNSUserEntityMeta hnsUserMeta;

	public HNSUserRepository(@NonNull final Config config) {
		entityQl = new Entityql(config);
		hnsUserMeta = new HNSUserEntityMeta();
	}

	public @NonNull List<HNSUserEntity> selectByPage(final int page) {
		return entityQl.from(hnsUserMeta)
				.orderBy(o -> o.desc(hnsUserMeta.points))
				.stream()
				.skip((long) BuildConfig.entriesPerPage * page)
				.limit(BuildConfig.entriesPerPage)
				.toList();
	}

	public @NonNull Optional<HNSUserEntity> selectByUUID(@NonNull final UUID uuid) {
		return entityQl.from(hnsUserMeta)
				.where(c -> c.eq(hnsUserMeta.uuid, uuid))
				.fetchOptional();
	}

	public @NonNull Status insert(@NonNull final HNSUserEntity hnsUser) {
		if (entityQl.from(hnsUserMeta)
				.where(w -> w.eq(hnsUserMeta.uuid, hnsUser.getUuid()))
				.fetchOptional()
				.isPresent()) return Status.DUPLICATE;
		return entityQl.insert(hnsUserMeta, hnsUser)
				.execute()
				.getCount() == 1 ? Status.SUCCESS : Status.ERROR;
	}

	public @NonNull Status update(@NonNull final HNSUserEntity hnsUser) {
		return entityQl.update(hnsUserMeta, hnsUser)
				.execute()
				.getCount() == 1 ? Status.SUCCESS : Status.ERROR;
	}

	public @NonNull Status deleteByUUID(@NonNull final UUID uuid) {
		return selectByUUID(uuid).filter(hnsUser -> entityQl.delete(hnsUserMeta, hnsUser)
						.execute()
						.getCount() == 1)
				.map(_ -> Status.SUCCESS)
				.orElse(Status.ERROR);
	}

	public int countEntries() {
		return entityQl.from(hnsUserMeta)
				.fetch()
				.size();
	}

	public int countPages() {
		return (int) Math.ceil((double) entityQl.from(hnsUserMeta)
				.fetch()
				.size() / BuildConfig.entriesPerPage);
	}
}
