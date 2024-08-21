package org.mintdaniel42.starediscordbot.data.repository;

import jakarta.inject.Singleton;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.UserEntity;
import org.mintdaniel42.starediscordbot.data.entity.UserEntityMeta;
import org.mintdaniel42.starediscordbot.utils.Status;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.criteria.Entityql;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public final class UserRepository {
	@NonNull private final Entityql entityQl;
	@NonNull private final UserEntityMeta userMeta;

	public UserRepository(@NonNull final Config config) {
		entityQl = new Entityql(config);
		userMeta = new UserEntityMeta();
	}

	public @NonNull Optional<UserEntity> selectByUUID(@NonNull final UUID uuid) {
		return entityQl.from(userMeta)
				.where(w -> w.eq(userMeta.uuid, uuid))
				.fetchOptional();
	}

	public @NonNull List<UserEntity> selectByGroupTag(@NonNull final String groupTag) {
		return entityQl.from(userMeta)
				.where(w -> w.eq(userMeta.groupTag, groupTag))
				.fetch();
	}

	public @NonNull Status insert(@NonNull final UserEntity user) {
		if (entityQl.from(userMeta)
				.where(w -> w.eq(userMeta.uuid, user.getUuid()))
				.fetchOptional()
				.isPresent()) return Status.DUPLICATE;
		return entityQl.insert(userMeta, user)
				.execute()
				.getCount() == 1 ? Status.SUCCESS : Status.ERROR;
	}

	public @NonNull Status update(@NonNull final UserEntity user) {
		return entityQl.update(userMeta, user)
				.execute()
				.getCount() == 1 ? Status.SUCCESS : Status.ERROR;
	}

	public @NonNull Status deleteByUUID(@NonNull final UUID uuid) {
		return selectByUUID(uuid).filter(user -> entityQl.delete(userMeta, user)
						.execute()
						.getCount() == 1)
				.map(_ -> Status.SUCCESS)
				.orElse(Status.ERROR);
	}
}
