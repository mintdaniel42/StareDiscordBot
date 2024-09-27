package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.ProfileEntity;
import org.mintdaniel42.starediscordbot.data.entity.ProfileEntityMeta;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public final class ProfileRepository extends BaseRepository<UUID, ProfileEntity> {
	public ProfileRepository(@NonNull final Entityql entityQl) {
		final var meta = new ProfileEntityMeta();
		super(entityQl, meta, meta.uuid);
	}

	public @NonNull List<ProfileEntity> selectByUsernameLike(@NonNull final String like) {
		return entityQl.from(meta)
				.where(w -> w.like(((ProfileEntityMeta) meta).username, "%%%s%%".formatted(like)))
				.fetch();
	}

	public @NonNull Optional<ProfileEntity> selectByUsername(@NonNull final String username) {
		return entityQl.from(meta)
				.where(w -> w.eq(((ProfileEntityMeta) meta).username, username))
				.fetchOptional();
	}

	public void deleteByAge(final long oldestTimestamp) {
		entityQl.from(meta)
				.where(w -> w.le(((ProfileEntityMeta) meta).lastUpdated, oldestTimestamp))
				.stream()
				.forEach(request -> entityQl.delete(meta, request));
	}
}
