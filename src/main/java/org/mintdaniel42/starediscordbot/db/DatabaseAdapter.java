package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.utils.Options;

import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@Slf4j
public final class DatabaseAdapter implements AutoCloseable {
    private static final MetaDataModel.Version dbVersion = MetaDataModel.Version.NEW_REQUESTS;
    private final int entriesPerPage = Options.getEntriesPerPage();
    @NotNull private final ConnectionSource connectionSource;
    private final Dao<HNSUserModel, UUID> hnsUserModelDao;
    private final Dao<PGUserModel, UUID> pgUserModelDao;
    private final Dao<RequestModel, Long> requestModelDao;
    private final Dao<UsernameModel, UUID> usernameModelDao;
    private final Dao<MetaDataModel, Integer> metaDataModelDao;

    public DatabaseAdapter(@NonNull String jdbcUrl) throws Exception {
        connectionSource = new JdbcPooledConnectionSource(jdbcUrl);

        hnsUserModelDao = DaoManager.createDao(connectionSource, HNSUserModel.class);
        pgUserModelDao = DaoManager.createDao(connectionSource, PGUserModel.class);
        requestModelDao = DaoManager.createDao(connectionSource, RequestModel.class);
        usernameModelDao = DaoManager.createDao(connectionSource, UsernameModel.class);
        metaDataModelDao = DaoManager.createDao(connectionSource, MetaDataModel.class);

        prepareDatabase();
        cleanDatabase();
    }

    private void prepareDatabase() {
	    try {
            TableUtils.createTableIfNotExists(connectionSource, MetaDataModel.class);
            if (metaDataModelDao.countOf() == 0) {
                metaDataModelDao.create(new MetaDataModel(MetaDataModel.Version.META));
            }
            while (metaDataModelDao.queryForFirst().getVersion() != dbVersion) {
                switch (metaDataModelDao.queryForFirst().getVersion()) {
                    case META -> {
                        try {
                            hnsUserModelDao.executeRawNoArgs("ALTER TABLE entries RENAME TO hns_entries");
                        } catch (SQLException ignored) {
                            TableUtils.createTableIfNotExists(connectionSource, HNSUserModel.class);
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
                        } catch(SQLException ignored) {}
                        TableUtils.createTableIfNotExists(connectionSource, RequestModel.class);
                        metaDataModelDao.update(new MetaDataModel(MetaDataModel.Version.NEW_REQUESTS));
                    }
                }
            }
	    } catch (SQLException e) {
		    log.error("Couldn't prepare database: ", e);
	    }
    }

    private void cleanDatabase() {
        // reschedule
        float next = Options.getCleanInterval() - System.currentTimeMillis() % Options.getCleanInterval();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cleanDatabase();
            }
        }, Math.round(next));
	    log.info("Schedule next database cleaning in {} seconds", next / 1000);

        // perform cleaning
        try {
            DeleteBuilder<RequestModel, Long> deleteBuilder = requestModelDao.deleteBuilder();
            deleteBuilder.where()
                    .le("timestamp", System.currentTimeMillis() - Options.getMaxRequestAge());
            requestModelDao.delete(deleteBuilder.prepare());

        } catch (SQLException e) {
            log.error("Could not clean database: ", e);
        }
    }

    public @Nullable List<HNSUserModel> getHnsUserList(int page) {
        try {
            return hnsUserModelDao.queryBuilder()
                    .orderByNullsLast("points", false)
                    .offset((long) entriesPerPage * page)
                    .limit((long) entriesPerPage)
                    .query()
                    .stream()
                    .toList();
        } catch (SQLException ignored) {
            return null;
        }
    }

    public @Nullable List<PGUserModel> getPgUserList(int page) {
        try {
            return pgUserModelDao.queryBuilder()
                    .orderByNullsLast("points", false)
                    .offset((long) entriesPerPage * page)
                    .limit((long) entriesPerPage)
                    .query()
                    .stream()
                    .toList();
        } catch (SQLException ignored) {
            return null;
        }
    }

    public @Nullable List<RequestModel> getPendingRequests() {
        try {
            return requestModelDao.queryForAll();
        } catch (SQLException ignored) {
            return null;
        }
    }

    public @Nullable List<UsernameModel> getUsernames(@NonNull String having) {
        try {
            return usernameModelDao.queryBuilder().where().like("username", "%" + having + "%").query();
        } catch (SQLException ignored) {
            return null;
        }
    }

    public @Nullable HNSUserModel getHnsUser(@NonNull UUID uuid) {
        try {
            return hnsUserModelDao.queryBuilder().where().eq("uuid", uuid).queryForFirst();
        } catch (SQLException ignored) {
            return null;
        }
    }

    public @Nullable PGUserModel getPgUser(@NonNull UUID uuid) {
        try {
            return pgUserModelDao.queryBuilder().where().eq("uuid", uuid).queryForFirst();
        } catch (SQLException ignored) {
            return null;
        }
    }

    public @Nullable UsernameModel getUsernameModel(@NonNull UUID uuid) {
        try {
            return usernameModelDao.queryForId(uuid);
        } catch (SQLException ignored) {
            return null;
        }
    }

    public @Nullable UsernameModel getUsernameModel(@NonNull String username) {
        try {
            return usernameModelDao.queryBuilder().where().eq("username", username).queryForFirst();
        } catch (SQLException ignored) {
            return null;
        }
    }

    public long getHnsPages() {
        try {
            return (long) Math.ceil((double) hnsUserModelDao.queryBuilder().countOf() / entriesPerPage);
        } catch (SQLException ignored) {
            return 0;
        }
    }

    public long getPgPages() {
        try {
            return (long) Math.ceil((double) pgUserModelDao.queryBuilder().countOf() / entriesPerPage);
        } catch (SQLException ignored) {
            return 0;
        }
    }

    public int editHnsUser(@NonNull HNSUserModel hnsUserModel) {
        try {
            return hnsUserModelDao.update(hnsUserModel);
        } catch (SQLException ignored) {
            return 0;
        }
    }

    public int editPgUser(@NonNull PGUserModel pgUserModel) {
        try {
            return pgUserModelDao.update(pgUserModel);
        } catch (SQLException ignored) {
            return 0;
        }
    }

    public void putUsername(@NonNull UsernameModel usernameModel) {
        try {
            usernameModelDao.createOrUpdate(usernameModel).getNumLinesChanged();
        } catch (SQLException ignored) {}
    }

    /**
     * @param hnsUserModel the {@link HNSUserModel} to be added
     * @return {@code true} if it was added, else {@code false}
     */
    public boolean addHnsUser(@NonNull HNSUserModel hnsUserModel) {
        try {
            return hnsUserModelDao.createIfNotExists(hnsUserModel).equals(hnsUserModel);
        } catch (SQLException ignored) {
            return false;
        }
    }

    /**
     * @param pgUserModel the {@link PGUserModel} to be added
     * @return {@code true} if it was added, else {@code false}
     */
    public boolean addPgUser(@NonNull PGUserModel pgUserModel) {
        try {
            return pgUserModelDao.createIfNotExists(pgUserModel).equals(pgUserModel);
        } catch (SQLException ignored) {
            return false;
        }
    }

    public boolean hasHnsUser(@NonNull UUID uuid) {
        try {
            return hnsUserModelDao.idExists(uuid);
        } catch (SQLException ignored) {
            return false;
        }
    }

    public boolean hasPgUser(@NonNull UUID uuid) {
        try {
            return pgUserModelDao.idExists(uuid);
        } catch (SQLException ignored) {
            return false;
        }
    }

    /**
     * @param requestModel the {@link RequestModel} to be added
     * @return {@code true} if it was added, else {@code false}
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean addRequest(@NonNull RequestModel requestModel) {
        try {
            return requestModelDao.createIfNotExists(requestModel).equals(requestModel);
        } catch (SQLException ignored) {
            return false;
        }
    }

    /**
     * Attempts to merge the request of the provided id into the database
     * @param id id of the request
     * @return {@code true} if request could be merged, false otherwise
     */
    public boolean mergeRequest(long id) {
        try {
            if (!requestModelDao.idExists(id)) return false;
            RequestModel requestModel = requestModelDao.queryForId(id);
            switch (requestModel.getDatabase()) {
                case HNS -> {
                    return (hnsUserModelDao.update(HNSUserModel.from(requestModel)) == 1) && (requestModelDao.deleteById(id) == 1);
                }
                case PG -> {
                    return (pgUserModelDao.update(PGUserModel.from(requestModel)) == 1) && (requestModelDao.deleteById(id) == 1);
                }
                default -> {
                    return false;
                }
            }
        } catch (SQLException e) {
            log.error("Request could not be merged: ", e);
            return false;
        }
    }

    public void close() throws Exception{
        connectionSource.close();
    }
}
