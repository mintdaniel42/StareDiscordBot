package org.mintdaniel42.starediscordbot.data.dao;

import org.seasar.doma.*;

@Dao
public interface MetaDataDao {
	@Sql("""
			CREATE TABLE IF NOT EXISTS metadata (
			    id INT PRIMARY KEY NOT NULL,
			    version VARCHAR(255) NOT NULL
			);
			""")
	@Script
	void createTable();

	@Sql("""
			DROP TABLE IF EXISTS metadata
			""")
	@Script
	void dropTable();

	@Sql("""
			PRAGMA user_version = /*^ version */'UNKNOWN'
			""")
	@Update
	@SuppressWarnings("UnusedReturnValue")
	int setVersion(final int version);

	@Sql("""
			PRAGMA user_version
			""")
	@Select
	int getVersion();
}
