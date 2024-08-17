package org.mintdaniel42.starediscordbot.data.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Script;
import org.seasar.doma.Sql;

@Dao
public interface GroupDao {
	@Sql("""
			CREATE TABLE IF NOT EXISTS groups (
			    tag VARCHAR(255) PRIMARY KEY NOT NULL,
			    name VARCHAR(255) NOT NULL,
			    leader CHAR(36) NOT NULL,
			    relation VARCHAR(255) NOT NULL
			);
			""")
	@Script
	void createTable();
}
