package org.mintdaniel42.starediscordbot.data.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Script;
import org.seasar.doma.Sql;

@Dao
public interface PGUserDao {
	@Sql("""
			CREATE TABLE IF NOT EXISTS pg_entries (
			    uuid CHAR(36) PRIMARY KEY NOT NULL,
			    rating VARCHAR(255) NOT NULL,
			    joined VARCHAR(255) NOT NULL,
			    points BIGINT NOT NULL,
			    luck DOUBLE NOT NULL,
			    quota DOUBLE NOT NULL,
			    winrate DOUBLE NOT NULL
			);
			""")
	@Script
	void createTable();
}
