package org.mintdaniel42.starediscordbot.data;

import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mintdaniel42.starediscordbot.Version;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.dao.MetaDataDao;
import org.mintdaniel42.starediscordbot.data.entity.*;
import org.mintdaniel42.starediscordbot.data.exception.NonExistentKeyException;
import org.mintdaniel42.starediscordbot.data.migration.UnknownMigration;
import org.mintdaniel42.starediscordbot.data.migration.V24Migration;
import org.mintdaniel42.starediscordbot.data.repository.*;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@RequiredArgsConstructor
@Singleton
@Slf4j
public final class Database implements AutoCloseable {
	private static final int targetVersion = Version.V2_4.ordinal();
	@NonNull private final UnknownMigration unknownMigration;
	@NonNull private final V24Migration v24Migration;
	@NonNull private final DatabaseConfig config;
	@Getter @NonNull private final AchievementRepository achievementRepository;
	@Getter @NonNull private final GroupRepository groupRepository;
	@Getter @NonNull private final HNSUserRepository hnsUserRepository;
	@Getter @NonNull private final MapRepository mapRepository;
	@Getter @NonNull private final MetaDataDao metaDataDao;
	@Getter @NonNull private final PGUserRepository pgUserRepository;
	@Getter @NonNull private final RequestRepository requestRepository;
	@Getter @NonNull private final SpotRepository spotRepository;
	@Getter @NonNull private final ProfileRepository profileRepository;
	@Getter @NonNull private final UserRepository userRepository;

	public void deleteUserData(@NonNull final UUID uuid) throws BotException {
		profileRepository.deleteById(uuid);
		try {
			userRepository.deleteById(uuid);
		} catch (BotException _) {
		}
		try {
			hnsUserRepository.deleteById(uuid);
		} catch (BotException _) {
		}
		try {
			pgUserRepository.deleteById(uuid);
		} catch (BotException _) {
		}
	}

	public void mergeRequest(final long id) throws BotException {
		final var request = requestRepository.selectById(id).orElseThrow(NonExistentKeyException::new);
		switch (request.getType()) {
			case hns -> hnsUserRepository.update(HNSUserEntity.from(request));
			case pg -> pgUserRepository.update(PGUserEntity.from(request));
			case user -> userRepository.update(UserEntity.from(request));
			case group -> groupRepository.update(GroupEntity.from(request));
		}
		requestRepository.deleteById(id);
	}

	public void prepareDatabase() {
		if (metaDataDao.getVersion() != targetVersion) runMigrations();
	}

	public void cleanDatabase() {
		// perform cleaning
		try {
			requestRepository.deleteByAge(System.currentTimeMillis() - BuildConfig.maxRequestAge);
			profileRepository.deleteByAge(System.currentTimeMillis() - BuildConfig.maxUsernameAge);
		} catch (RuntimeException e) {
			log.error(R.Strings.log("could_not_clean_database"), e);
		}

		// automatically fetch usernames after cleaning the database from old ones
		if (BuildConfig.autoFetch) {
			log.info(R.Strings.log("autofetching_usernames"));
			var fetched = 0;
			try {
				for (UserEntity userEntity : userRepository.selectAll()) {
					try {
						if (profileRepository.has(userEntity.getUuid())) continue;
						if (MCHelper.getUsername(profileRepository, userEntity.getUuid()) instanceof String username) {
							profileRepository.insert(ProfileEntity.builder()
									.uuid(userEntity.getUuid())
									.username(username)
									.lastUpdated(System.currentTimeMillis())
									.build());
							fetched++;
						}
					} catch (BotException | RuntimeException e) {
						log.error(R.Strings.log("could_not_autofetch_usernames"), e);
					}
				}
			} catch (RuntimeException e) {
				log.error(R.Strings.log("could_not_autofetch_usernames"), e);
			} finally {
				log.info(R.Strings.log("autofetched_s_usernames", fetched));
			}
		}
	}

	@Override
	public void close() throws Exception {
		config.close();
	}

	private void runMigrations() {
		int version = metaDataDao.getVersion();
		do {
			version = switch (version) {
				case 0 -> unknownMigration.apply(version);
				case 1 -> v24Migration.apply(version);
				default -> targetVersion;
			};
		} while (version != targetVersion);
		metaDataDao.setVersion(version);
	}
}