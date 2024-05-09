package org.mintdaniel42.starediscordbot.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.mintdaniel42.starediscordbot.utils.Options;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Slf4j
public final class DatabaseAdapter implements AutoCloseable {
    private static final int dbVersion = 2;
    private final int entriesPerPage = Options.getEntriesPerPage();
    private final JdbcPooledConnectionSource connectionSource;
    private final Dao<HNSUserModel, UUID> hnsUserModelDao;
    private final Dao<PGUserModel, UUID> pgUserModelDao;
    private final Dao<RequestModel, Long> requestModelDao;
    private final Dao<UsernameModel, UUID> usernameModelDao;

    public DatabaseAdapter(@NonNull String jdbcUrl) throws Exception {
        prepareDatabase(jdbcUrl);

        connectionSource = new JdbcPooledConnectionSource(jdbcUrl);

        hnsUserModelDao = DaoManager.createDao(connectionSource, HNSUserModel.class);
        pgUserModelDao = DaoManager.createDao(connectionSource, PGUserModel.class);
        requestModelDao = DaoManager.createDao(connectionSource, RequestModel.class);
        usernameModelDao = DaoManager.createDao(connectionSource, UsernameModel.class);

        TableUtils.createTableIfNotExists(connectionSource, HNSUserModel.class);
        TableUtils.createTableIfNotExists(connectionSource, PGUserModel.class);
        TableUtils.createTableIfNotExists(connectionSource, RequestModel.class);
        TableUtils.createTableIfNotExists(connectionSource, UsernameModel.class);
    }

    private void prepareDatabase(@NonNull String jdbcUrl) {
        try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
            ResultSet result = conn.createStatement().executeQuery("PRAGMA user_version");

            int userVersion;

            if (result.next() && (userVersion = result.getInt(1)) < dbVersion) while (dbVersion > userVersion) {
                switch (userVersion) {
                    case 1 -> {
                        conn.createStatement().executeQuery("ALTER TABLE entries RENAME TO hns_entries");
                        userVersion++;
                        conn.createStatement().executeQuery("PRAGMA user_version = " + userVersion);
                        log.debug("Upgraded database to version: {}", userVersion);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
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
                    .filter(usernameModelDao -> usernameModelDao.getUuid() != null)
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
                    .filter(usernameModelDao -> usernameModelDao.getUuid() != null)
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

    public boolean addHnsUser(@NonNull HNSUserModel hnsUserModel) {
        try {
            return hnsUserModelDao.createIfNotExists(hnsUserModel).equals(hnsUserModel);
        } catch (SQLException ignored) {
            return false;
        }
    }

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

    public boolean addRequest(@NonNull RequestModel requestModel) {
        try {
            return requestModelDao.createIfNotExists(requestModel).equals(requestModel);
        } catch (SQLException ignored) {
            return false;
        }
    }

    public boolean mergeRequest(long id) {
        return false;
    }

    public void close() throws Exception{
        connectionSource.close();
    }
}
