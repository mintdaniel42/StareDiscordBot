package org.mintdaniel42.starediscordbot.data.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Script;
import org.seasar.doma.Sql;

@Dao
public interface UserDao {
	@Sql("""
			CREATE TABLE IF NOT EXISTS users (
			    uuid CHAR(36) PRIMARY KEY NOT NULL,
			    group_id VARCHAR(255),
			    discord BIGINT NOT NULL,
			    note VARCHAR(255) NOT NULL
			);
			""")
	@Script
	void createTable();
}
