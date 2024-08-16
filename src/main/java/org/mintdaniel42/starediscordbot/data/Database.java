package org.mintdaniel42.starediscordbot.data;

import lombok.Getter;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.data.entity.GroupEntity;
import org.mintdaniel42.starediscordbot.data.entity.HNSUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.PGUserEntity;
import org.mintdaniel42.starediscordbot.data.entity.UserEntity;
import org.mintdaniel42.starediscordbot.data.repository.*;
import org.mintdaniel42.starediscordbot.utils.Status;
import org.seasar.doma.jdbc.Config;

import java.util.UUID;

@Getter
public final class Database {
	@NonNull private final Config config;
	@NonNull private final AchievementRepository achievementRepository;
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final MapRepository mapRepository;
	@NonNull private final MetaDataRepository metaDataRepository;
	@NonNull private final PGUserRepository pgUserRepository;
	@NonNull private final RequestRepository requestRepository;
	@NonNull private final UsernameRepository usernameRepository;
	@NonNull private final UserRepository userRepository;

	public Database(@NonNull final Config config) {
		this.config = config;
		achievementRepository = new AchievementRepository(config);
		groupRepository = new GroupRepository(config);
		hnsUserRepository = new HNSUserRepository(config);
		mapRepository = new MapRepository(config);
		metaDataRepository = new MetaDataRepository(config);
		pgUserRepository = new PGUserRepository(config);
		requestRepository = new RequestRepository(config);
		usernameRepository = new UsernameRepository(config);
		userRepository = new UserRepository(config);
	}

	// TODO
	public @NonNull Status deleteUserData(@NonNull final UUID uuid) {
		return Status.ERROR;
	}

	public @NonNull Status mergeRequest(final long id) {
		final var requestOptional = requestRepository.selectById(id);
		if (requestOptional.isPresent()) {
			final var request = requestOptional.get();
			return switch (switch (request.getType()) {
				case hns -> hnsUserRepository.update(HNSUserEntity.from(request));
				case pg -> pgUserRepository.update(PGUserEntity.from(request));
				case user -> userRepository.update(UserEntity.from(request));
				case group -> groupRepository.update(GroupEntity.from(request));
			}) {
				case SUCCESS -> requestRepository.deleteById(id);
				case ERROR, DUPLICATE -> Status.ERROR;
			};
		} else return Status.ERROR;
	}

	// TODO
	public void prepareDatabase() {
	}

	// TODO
	public void cleanDatabase() {
	}
}