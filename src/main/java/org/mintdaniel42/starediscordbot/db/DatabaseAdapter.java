package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.build.BuildConfig;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.R;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
public final class DatabaseAdapter implements AutoCloseable {
    @NonNull private final ConnectionSource connectionSource;
    private final Dao<HNSUserModel, UUID> hnsUserModelDao;
    private final Dao<PGUserModel, UUID> pgUserModelDao;
    private final Dao<RequestModel, Long> requestModelDao;
    private final Dao<UsernameModel, UUID> usernameModelDao;
    private final Dao<UserModel, UUID> userModelDao;
    private final Dao<GroupModel, String> groupModelDao;
    private final Dao<MetaDataModel, Integer> metaDataModelDao;

    public DatabaseAdapter(@NonNull String jdbcUrl) throws Exception {
        connectionSource = new JdbcPooledConnectionSource(jdbcUrl);

        hnsUserModelDao = DaoManager.createDao(connectionSource, HNSUserModel.class);
        pgUserModelDao = DaoManager.createDao(connectionSource, PGUserModel.class);
        requestModelDao = DaoManager.createDao(connectionSource, RequestModel.class);
        usernameModelDao = DaoManager.createDao(connectionSource, UsernameModel.class);
        userModelDao = DaoManager.createDao(connectionSource, UserModel.class);
        groupModelDao = DaoManager.createDao(connectionSource, GroupModel.class);
        metaDataModelDao = DaoManager.createDao(connectionSource, MetaDataModel.class);

        prepareDatabase();
        new Thread(this::cleanDatabase).start();
    }

    private void prepareDatabase() {
	    try {
            TableUtils.createTableIfNotExists(connectionSource, MetaDataModel.class);
            if (metaDataModelDao.countOf() == 0) {
                metaDataModelDao.create(new MetaDataModel(MetaDataModel.Version.META));
            }
            boolean hnsUserModelV2 = false;
            while (metaDataModelDao.queryForFirst().getVersion() != MetaDataModel.Version.values()[MetaDataModel.Version.values().length - 1]) {
                switch (metaDataModelDao.queryForFirst().getVersion()) {
                    case META -> {
                        try {
                            hnsUserModelDao.executeRawNoArgs("ALTER TABLE entries RENAME TO hns_entries");
                        } catch (SQLException _) {
                            TableUtils.createTableIfNotExists(connectionSource, HNSUserModel.class);
                            hnsUserModelV2 = true;
                        }
                        metaDataModelDao.update(new MetaDataModel(MetaDataModel.Version.HNS_ONLY));
                    }
                    case HNS_ONLY -> {
                        TableUtils.createTableIfNotExists(connectionSource, PGUserModel.class);
                        metaDataModelDao.update(new MetaDataModel(MetaDataModel.Version.PG_ADDED));
                    }
                    case PG_ADDED -> {
                        TableUtils.createTableIfNotExists(connectionSource, UsernameModel.class);
                        metaDataModelDao.update(new MetaDataModel(MetaDataModel.Version.USERNAMES_ADDED));
                    }
                    case USERNAMES_ADDED -> {
                        try {
                            requestModelDao.executeRawNoArgs("DROP TABLE requests");
                        } catch(SQLException e) {
                            log.error(R.Strings.log("could_not_perform_s_migration", "new_requests"), e);
                            throw new RuntimeException(e);
                        }
                        TableUtils.createTableIfNotExists(connectionSource, RequestModel.class);
                        metaDataModelDao.update(new MetaDataModel(MetaDataModel.Version.NEW_REQUESTS));
                    }
                    case NEW_REQUESTS -> {
                        TableUtils.createTableIfNotExists(connectionSource, GroupModel.class);
                        TableUtils.createTableIfNotExists(connectionSource, UserModel.class);
                        try {
                            requestModelDao.executeRawNoArgs("ALTER TABLE requests ADD COLUMN tag VARCHAR");
                            requestModelDao.executeRawNoArgs("ALTER TABLE requests ADD COLUMN name VARCHAR");
                            requestModelDao.executeRawNoArgs("ALTER TABLE requests ADD COLUMN leader VARCHAR");
                            requestModelDao.executeRawNoArgs("ALTER TABLE requests ADD COLUMN relation VARCHAR");
                            requestModelDao.executeRawNoArgs("ALTER TABLE requests ADD COLUMN \"group\" VARCHAR");
                            requestModelDao.executeRawNoArgs("ALTER TABLE requests ADD COLUMN discord BIGINT");
                            metaDataModelDao.update(new MetaDataModel(MetaDataModel.Version.GROUPS_ADDED));
                        } catch (SQLException e) {
                            log.error(R.Strings.log("could_not_perform_s_migration", "groups_added"), e);
                            throw new RuntimeException(e);
                        }
                    }
                    case GROUPS_ADDED -> {
                        try {
                            requestModelDao.executeRawNoArgs("ALTER TABLE requests RENAME COLUMN \"group\" TO group_id");
                            requestModelDao.executeRawNoArgs("ALTER TABLE requests ADD COLUMN top10 VARCHAR DEFAULT ❌");
                            requestModelDao.executeRawNoArgs("ALTER TABLE requests ADD COLUMN streak INTEGER");
                            requestModelDao.executeRawNoArgs("ALTER TABLE requests ADD COLUMN highestRank VARCHAR DEFAULT ❌");
                            requestModelDao.executeRawNoArgs("ALTER TABLE requests ADD COLUMN note VARCHAR DEFAULT ❌");
                            if (!hnsUserModelV2) {
                                hnsUserModelDao.executeRawNoArgs("ALTER TABLE hns_entries ADD COLUMN top10 VARCHAR DEFAULT ❌");
                                hnsUserModelDao.executeRawNoArgs("ALTER TABLE hns_entries ADD COLUMN streak INTEGER");
                                hnsUserModelDao.executeRawNoArgs("ALTER TABLE hns_entries ADD COLUMN highestRank VARCHAR DEFAULT ❌");
                            }

                            List<UUID> uuids = Stream.of(hnsUserModelDao.queryForAll(), pgUserModelDao.queryForAll())
                                    .flatMap(Collection::stream)
                                    .map(object -> {
                                        if (object instanceof HNSUserModel hnsUserModel) return hnsUserModel.getUuid();
                                        else if (object instanceof PGUserModel pgUserModel) return pgUserModel.getUuid();
                                        else return null;
                                    })
                                    .filter(Objects::nonNull)
                                    .distinct()
                                    .toList();

                            for (UUID uuid : uuids) {
                                UserModel.UserModelBuilder builder = UserModel.builder();
                                builder.uuid(uuid);
                                if (hnsUserModelDao.idExists(uuid)) builder.hnsUser(hnsUserModelDao.queryForId(uuid));
                                if (pgUserModelDao.idExists(uuid)) builder.pgUser(pgUserModelDao.queryForId(uuid));
                                userModelDao.create(builder.build());
                            }

                            metaDataModelDao.update(new MetaDataModel(MetaDataModel.Version.HNS_V2));
                        } catch (SQLException e) {
                            log.error(R.Strings.log("could_not_perform_s_migration", "hns_v2"), e);
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
	    } catch (SQLException e) {
            log.error(R.Strings.log("could_not_prepare_database"), e);
            throw new RuntimeException(e);
	    }
    }

    private void cleanDatabase() {
        // perform cleaning
        try {
            var requestDeleteBuilder = requestModelDao.deleteBuilder();
            requestDeleteBuilder.where().le("timestamp", System.currentTimeMillis() - BuildConfig.maxRequestAge);
            requestDeleteBuilder.delete();

            var usernameDeleteBuilder = usernameModelDao.deleteBuilder();
            usernameDeleteBuilder.where().le("lastupdated", System.currentTimeMillis() - BuildConfig.maxUsernameAge);
            usernameDeleteBuilder.delete();

        } catch (SQLException e) {
            log.error(R.Strings.log("could_not_clean_database"), e);
        }

        // automatically fetch usernames after cleaning the database from old ones
        if (BuildConfig.autoFetch) {
            log.info(R.Strings.log("autofetching_usernames"));
            var fetched = 0;
            try {
                for (UserModel userModel : userModelDao.queryForAll()) {
                    try {
                        if (usernameModelDao.idExists(userModel.getUuid())) continue;
                        if (MCHelper.getUsername(userModel.getUuid()) instanceof String username) {
                            usernameModelDao.create(UsernameModel.builder()
                                    .uuid(userModel.getUuid())
                                    .username(username)
                                    .lastUpdated(System.currentTimeMillis())
                                    .build());
                            fetched++;
                        }
                    } catch (SQLException e) {
                        log.error(R.Strings.log("could_not_autofetch_usernames"), e);
                    }
                }
            } catch (SQLException e) {
                log.error(R.Strings.log("could_not_autofetch_usernames"), e);
            } finally {
                log.info(R.Strings.log("autofetched_s_usernames", fetched));
            }
        }

        // reschedule
        float next = BuildConfig.cleaningInterval - System.currentTimeMillis() % BuildConfig.cleaningInterval;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cleanDatabase();
            }
        }, Math.round(next));
        log.info(R.Strings.log("schedule_next_database_cleaning_in_s_seconds", next / 1000));
    }

    public @Nullable List<HNSUserModel> getHnsUserList(int page) {
        try {
            return hnsUserModelDao.queryBuilder()
                    .orderByNullsLast("points", false)
                    .offset((long) BuildConfig.entriesPerPage * page)
                    .limit((long) BuildConfig.entriesPerPage)
                    .query()
                    .stream()
                    .toList();
        } catch (SQLException _) {
            return null;
        }
    }

    public @Nullable List<PGUserModel> getPgUserList(int page) {
        try {
            return pgUserModelDao.queryBuilder()
                    .orderByNullsLast("points", false)
                    .offset((long) BuildConfig.entriesPerPage * page)
                    .limit((long) BuildConfig.entriesPerPage)
                    .query()
                    .stream()
                    .toList();
        } catch (SQLException _) {
            return null;
        }
    }

    public @Nullable List<RequestModel> getPendingRequests() {
        try {
            return requestModelDao.queryForAll();
        } catch (SQLException _) {
            return null;
        }
    }

    public @Nullable List<UsernameModel> getUsernames(@NonNull String having) {
        try {
            return usernameModelDao.queryBuilder().where().like("username", "%" + having + "%").query();
        } catch (SQLException _) {
            return null;
        }
    }

    public @Nullable List<GroupModel> getGroups(@NonNull final String having) {
        try {
            return groupModelDao.queryBuilder()
                    .where()
                    .like("name", "%%%s%%".formatted(having))
                    .or()
                    .like("tag", "%%%s%%".formatted(having))
                    .query();
        } catch (SQLException _) {
            return null;
        }
    }

    public @Nullable HNSUserModel getHnsUser(@NonNull UUID uuid) {
        try {
            return hnsUserModelDao.queryBuilder().where().eq("uuid", uuid).queryForFirst();
        } catch (SQLException _) {
            return null;
        }
    }

    public @Nullable PGUserModel getPgUser(@NonNull UUID uuid) {
        try {
            return pgUserModelDao.queryBuilder().where().eq("uuid", uuid).queryForFirst();
        } catch (SQLException _) {
            return null;
        }
    }

    public @Nullable GroupModel getGroup(@NonNull String tag) {
        try {
            return groupModelDao.queryBuilder()
                    .where()
                    .eq("tag", tag)
                    .queryForFirst()
                    .toBuilder()
                    .members(userModelDao.queryBuilder()
                            .where()
                            .eq("group_id", tag)
                            .query()
                            .stream()
                            .map(userModel -> userModel.toBuilder()
                                    .username(MCHelper.getUsername(this, userModel.getUuid()))
                                    .hnsUser(getHnsUser(userModel.getUuid()))
                                    .pgUser(getPgUser(userModel.getUuid()))
                                    .build())
                            .toList())
                    .build();
        } catch (SQLException | NullPointerException _) {
            return null;
        }
    }

    public @Nullable UserModel getUser(@NonNull UUID uuid) {
        try {
            UserModel userModel = userModelDao.queryBuilder()
                    .where()
                    .eq("uuid", uuid)
                    .queryForFirst();
            return userModel == null ? null : userModel
                    .toBuilder()
                    .username(MCHelper.getUsername(this, uuid))
                    .hnsUser(getHnsUser(uuid))
                    .pgUser(getPgUser(uuid))
                    .build();
        } catch (SQLException _) {
            return null;
        }
    }

    public @Nullable UsernameModel getUsernameModel(@NonNull UUID uuid) {
        try {
            return usernameModelDao.queryForId(uuid);
        } catch (SQLException _) {
            return null;
        }
    }

    public @Nullable UsernameModel getUsernameModel(@NonNull String username) {
        try {
            return usernameModelDao.queryBuilder().where().eq("username", username).queryForFirst();
        } catch (SQLException _) {
            return null;
        }
    }

    /**
     * Get the number of hide 'n' seek pages.
     * This uses the {@link BuildConfig}.{@code entriesPerPage} constant
     *
     * @return the number of pages
     */
    public long getHnsPages() {
        try {
            return (long) Math.ceil((double) hnsUserModelDao.queryBuilder().countOf() / BuildConfig.entriesPerPage);
        } catch (SQLException _) {
            return 0;
        }
    }

    /**
     * Get the number of partygames pages.
     * This uses the {@link BuildConfig}.{@code entriesPerPage} constant
     *
     * @return the number of pages
     */
    public long getPgPages() {
        try {
            return (long) Math.ceil((double) pgUserModelDao.queryBuilder().countOf() / BuildConfig.entriesPerPage);
        } catch (SQLException _) {
            return 0;
        }
    }

    /**
     * Get the number of group member pages for a group.
     * This uses the {@link BuildConfig}.{@code entriesPerPage} constant
     *
     * @param tag the group tag to calculate the pages for
     * @return the number of pages if the group was found, 0 otherwise
     */
    public long getGroupMemberPages(@NonNull final String tag) {
        try {
            return (long) Math.ceil((double) userModelDao.queryBuilder()
		            .where()
		            .eq("group_id", tag)
		            .countOf() / BuildConfig.entriesPerPage);
        } catch (SQLException _) {
            return 0;
        }
    }

    /**
     * Write a {@link UsernameModel} to the cache regardless if it already exists or not
     * @param usernameModel the {@link UsernameModel} to be written
     */
    public void putUsername(@NonNull UsernameModel usernameModel) {
        try {
            usernameModelDao.createOrUpdate(usernameModel);
        } catch (SQLException _) {}
    }

    /**
     * @param hnsUserModel the {@link HNSUserModel} to be added
     * @return {@code true} if it was added, {@code false} otherwise
     */
    public boolean addHnsUser(@NonNull HNSUserModel hnsUserModel) {
        try {
            if (!userModelDao.idExists(hnsUserModel.getUuid())) userModelDao.create(UserModel.builder()
                    .uuid(hnsUserModel.getUuid())
                    .build());
            return hnsUserModelDao.createIfNotExists(hnsUserModel).equals(hnsUserModel);
        } catch (SQLException _) {
            return false;
        }
    }

    /**
     * @param pgUserModel the {@link PGUserModel} to be added
     * @return {@code true} if it was added, {@code false} otherwise
     */
    public boolean addPgUser(@NonNull PGUserModel pgUserModel) {
        try {
            if (!userModelDao.idExists(pgUserModel.getUuid())) userModelDao.create(UserModel.builder()
                    .uuid(pgUserModel.getUuid())
                    .build());
            return pgUserModelDao.createIfNotExists(pgUserModel).equals(pgUserModel);
        } catch (SQLException _) {
            return false;
        }
    }

    /**
     * @param groupModel the {@link GroupModel} to be added
     * @return {@code true} if it was added, {@code false} otherwise
     */
    public boolean addGroup(@NonNull GroupModel groupModel) {
        try {
            return groupModelDao.createIfNotExists(groupModel).equals(groupModel);
        } catch (SQLException _) {
            return false;
        }
    }

    /**
     * @param userModel the {@link UserModel} to be added
     * @return {@code true} if it was added, {@code false} otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean addUser(@NonNull UserModel userModel) {
        try {
            if (userModel.getPgUser() != null) addPgUser(userModel.getPgUser());
            if (userModel.getHnsUser() != null) addHnsUser(userModel.getHnsUser());
            return userModelDao.createIfNotExists(userModel).equals(userModel);
        } catch (SQLException _) {
            return false;
        }
    }

    /**
     * Checks if a user has a corresponding hide 'n' seek entry
     * @param uuid the Minecraft UUID of the user to check for
     * @return {@code true} if the hide 'n' seek entry exists, {@code false} otherwise
     */
    public boolean hasHnsUser(@NonNull UUID uuid) {
        try {
            return hnsUserModelDao.idExists(uuid);
        } catch (SQLException _) {
            return false;
        }
    }

    /**
     * Checks if a user has a corresponding Partygames entry
     * @param uuid the Minecraft UUID of the user to check for
     * @return {@code true} if the partygames entry exists, {@code false} otherwise
     */
    public boolean hasPgUser(@NonNull UUID uuid) {
        try {
            return pgUserModelDao.idExists(uuid);
        } catch (SQLException _) {
            return false;
        }
    }

    /**
     * Checks if a group exists
     * @param tag the group tag to check for if it exists
     * @return {@code true} if it exists, {@code false} otherwise
     */
    public boolean hasGroup(@NonNull String tag) {
        try {
            return groupModelDao.idExists(tag);
        } catch (SQLException _) {
            return false;
        }
    }

    /**
     * Checks if a user is in a group
     * @param uuid the Minecraft UUID of the user to check for
     * @return {@code true} if the given user is in a group, {@code false} otherwise
     */
    public boolean hasGroupFor(@NonNull UUID uuid) {
        try {
            return userModelDao.idExists(uuid) && userModelDao.queryBuilder()
                    .where()
                    .idEq(uuid)
                    .and()
                    .isNotNull("group_id")
                    .countOf() != 0;
        } catch (SQLException _) {
            return false;
        }
    }

    /**
     * @param requestModel the {@link RequestModel} to be added
     * @return {@code true} if it was added, {@code false} otherwise
     */
    public boolean addRequest(@NonNull RequestModel requestModel) {
        try {
            return requestModelDao.createIfNotExists(requestModel).equals(requestModel);
        } catch (SQLException _) {
            return false;
        }
    }

    /**
     * Attempts to replace the current model of type {@code T} with a new one
     * @param model the new model. This has to be one of:
     *              {@link HNSUserModel}, {@link PGUserModel}, {@link GroupModel} or {@link UserModel}
     * @return {@code true} if it could be edited, {@code false} otherwise
     */
    public <T> boolean edit(@NonNull T model) {
        try {
            return switch(model) {
                case HNSUserModel hnsUserModel -> hnsUserModelDao.update(hnsUserModel) != 0;
                case PGUserModel pgUserModel -> pgUserModelDao.update(pgUserModel) != 0;
                case GroupModel groupModel -> groupModelDao.update(groupModel) != 0;
                case UserModel userModel -> userModelDao.update(userModel) != 0;
                default -> false;
            };
        } catch (SQLException _) {
            return false;
        }
    }

    /**
     * Attempts to delete all user data including username cache
     * @param uuid the Minecraft user UUID
     * @return {@code true} if everything could be deleted, {@code false} otherwise
     */
    public boolean deleteUser(@NonNull UUID uuid) {
        try {
            byte sum = 0;
            if (!hnsUserModelDao.idExists(uuid)) sum++;
            else sum += (byte) hnsUserModelDao.deleteById(uuid);
            if (!pgUserModelDao.idExists(uuid)) sum++;
            else sum += (byte) pgUserModelDao.deleteById(uuid);
            if (!usernameModelDao.idExists(uuid)) sum++;
            else sum += (byte) usernameModelDao.deleteById(uuid);
            sum += (byte) userModelDao.deleteById(uuid);
            return sum == 4;
        } catch (SQLException _) {
            return false;
        }
    }

    /**
     * Attempts to delete the request of the provided id from the database
     * @param id id of the request
     * @return {@code true} if it could be deleted, {@code false} otherwise
     */
    public boolean deleteRequest(final long id) {
        try {
            return requestModelDao.deleteById(id) == 1;
        } catch (SQLException _) {
            return false;
        }
    }

    /**
     * Attempts to delete a group
     *
     * @param tag the group tag of the group to delete
     * @return {@code true} if it could be deleted, {@code false} otherwise
     */
    public boolean deleteGroup(String tag) {
        try {
            final var updateBuilder = userModelDao.updateBuilder();
            updateBuilder
                    .where()
                    .eq("group_id", tag);
            updateBuilder.updateColumnValue("group_id", null).update();
            return groupModelDao.deleteById(tag) == 1;
        } catch (SQLException _) {
            return false;
        }
    }

    /**
     * Attempts to merge the request of the provided id into the database
     * @param id id of the request
     * @return {@code true} if request could be merged, {@code false} otherwise
     */
    public boolean mergeRequest(long id) {
        try {
            if (!requestModelDao.idExists(id)) return false;
            RequestModel requestModel = requestModelDao.queryForId(id);
            switch (requestModel.getDatabase()) {
                case HNS -> {
                    return (hnsUserModelDao.update(HNSUserModel.from(requestModel)) == 1) && (requestModelDao.deleteById(id) == 1);
                } case PG -> {
                    return (pgUserModelDao.update(PGUserModel.from(requestModel)) == 1) && (requestModelDao.deleteById(id) == 1);
                } case USER -> {
                    return (userModelDao.update(UserModel.from(requestModel)) == 1) && (requestModelDao.deleteById(id) == 1);
                } case GROUP -> {
                    return (groupModelDao.update(GroupModel.from(requestModel)) == 1) && (requestModelDao.deleteById(id) == 1);
                } default -> {
                    return false;
                }
            }
        } catch (SQLException e) {
            log.error(R.Strings.log("could_not_merge_request"), e);
            return false;
        }
    }

    /**
     * Attempts to close the database connection
     * @throws Exception if closing fails
     */
    public void close() throws Exception{
        connectionSource.close();
    }
}
