package org.mintdaniel42.starediscordbot.data;

import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.data.entity.*;
import org.mintdaniel42.starediscordbot.data.exceptions.NonExistentKeyException;
import org.mintdaniel42.starediscordbot.data.repository.*;
import org.mintdaniel42.starediscordbot.exception.BotException;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@Singleton
@Slf4j
public final class Database implements AutoCloseable {
	@NonNull private static final MetaDataEntity.Version targetVersion = MetaDataEntity.Version.V3;
	@NonNull private final DatabaseConfig config;
	@NonNull private final Migrator migrator;
	@NonNull private final AchievementRepository achievementRepository;
	@NonNull private final GroupRepository groupRepository;
	@NonNull private final HNSUserRepository hnsUserRepository;
	@NonNull private final MapRepository mapRepository;
	@NonNull private final MetaDataRepository metaDataRepository;
	@NonNull private final PGUserRepository pgUserRepository;
	@NonNull private final RequestRepository requestRepository;
	@NonNull private final SpotRepository spotRepository;
	@NonNull private final ProfileRepository profileRepository;
	@NonNull private final UserRepository userRepository;

	public void deleteUserData(@NonNull final UUID uuid) throws BotException {
		userRepository.deleteById(uuid);
		profileRepository.deleteById(uuid);
		hnsUserRepository.deleteById(uuid);
		pgUserRepository.deleteById(uuid);
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

	public void prepareDatabase() throws BotException {
		migrator.onUpgrade(metaDataRepository.selectFirst().version(), targetVersion);
		metaDataRepository.upsert(new MetaDataEntity(0, targetVersion));
	}

	public void cleanDatabase() throws BotException {
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
					} catch (RuntimeException e) {
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
}