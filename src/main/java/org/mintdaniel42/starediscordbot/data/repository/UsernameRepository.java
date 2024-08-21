package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.UsernameEntity;
import org.mintdaniel42.starediscordbot.data.entity.UsernameEntityMeta;
import org.mintdaniel42.starediscordbot.utils.Status;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public final class UsernameRepository {
	@NonNull private final Entityql entityQl;
	@NonNull private final UsernameEntityMeta usernameMeta;

	public UsernameRepository(@NonNull final Config config) {
		entityQl = new Entityql(config);
		usernameMeta = new UsernameEntityMeta();
	}

	public @NonNull List<UsernameEntity> selectByUsernameLike(@NonNull final String like) {
		return entityQl.from(usernameMeta)
				.where(w -> w.like(usernameMeta.username, "%%%s%%".formatted(like)))
				.fetch();
	}

	public @NonNull Optional<UsernameEntity> selectByUUID(@NonNull final UUID uuid) {
		return entityQl.from(usernameMeta)
				.where(w -> w.eq(usernameMeta.uuid, uuid))
				.fetchOptional();
	}

	public @NonNull Optional<UsernameEntity> selectByUsername(@NonNull final String username) {
		return entityQl.from(usernameMeta)
				.where(w -> w.eq(usernameMeta.username, username))
				.fetchOptional();
	}

	public @NonNull Status insert(@NonNull final UsernameEntity username) {
		if (entityQl.from(usernameMeta)
				.where(w -> w.eq(usernameMeta.uuid, username.getUuid()))
				.fetchOptional()
				.isPresent()) {
			return entityQl.update(usernameMeta, username)
					.execute()
					.getCount() == 1 ? Status.SUCCESS : Status.ERROR;
		} else return entityQl.insert(usernameMeta, username)
				.execute()
				.getCount() == 1 ? Status.SUCCESS : Status.ERROR;
	}

	public @NonNull Status deleteByUUID(@NonNull final UUID uuid) {
		return selectByUUID(uuid).filter(username -> entityQl.delete(usernameMeta, username)
						.execute()
						.getCount() == 1)
				.map(_ -> Status.SUCCESS)
				.orElse(Status.ERROR);
	}

	public int countEntries() {
		return entityQl.from(usernameMeta)
				.fetch()
				.size();
	}
}
