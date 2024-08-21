package org.mintdaniel42.starediscordbot.data;

import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mintdaniel42.starediscordbot.data.dao.*;
import org.mintdaniel42.starediscordbot.data.entity.*;
import org.mintdaniel42.starediscordbot.data.repository.*;
import org.mintdaniel42.starediscordbot.utils.Status;
import org.seasar.doma.jdbc.Config;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@Singleton
public final class Database {
	@NonNull private final Config config;
	@NonNull private final AchievementRepository achievementRepository;
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final MapRepository mapRepository;
	@NonNull private final MetaDataRepository metaDataRepository;
	@NonNull private final PGUserRepository pgUserRepository;
	@NonNull private final RequestRepository requestRepository;
	@NonNull private final SpotRepository spotRepository;
	@NonNull private final UsernameRepository usernameRepository;
	@NonNull private final UserRepository userRepository;

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

	public void prepareDatabase() {
		new AchievementDaoImpl(config).createTable();
		new GroupDaoImpl(config).createTable();
		new HNSUserDaoImpl(config).createTable();
		new MetaDataDaoImpl(config).createTable();
		new PGUserDaoImpl(config).createTable();
		new SpotDaoImpl(config).createTable();
		new UserDaoImpl(config).createTable();
		new UsernameDaoImpl(config).createTable();
		metaDataRepository.insertOrUpdate(new MetaDataEntity(0, MetaDataEntity.Version.UNKNOWN));
	}

	// TODO
	public void cleanDatabase() {
	}
}