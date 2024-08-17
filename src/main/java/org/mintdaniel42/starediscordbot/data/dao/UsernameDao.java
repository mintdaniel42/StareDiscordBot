package org.mintdaniel42.starediscordbot.data.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Script;
import org.seasar.doma.Sql;

@Dao
public interface UsernameDao {
	@Sql("""
			CREATE TABLE IF NOT EXISTS usernames (
			    uuid CHAR(36) PRIMARY KEY NOT NULL,
			    username VARCHAR(255) NOT NULL,
			    lastUpdated BIGINT NOT NULL
			);
			""")
	@Script
	void createTable();
}
