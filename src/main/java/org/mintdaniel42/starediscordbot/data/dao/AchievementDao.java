package org.mintdaniel42.starediscordbot.data.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Script;
import org.seasar.doma.Sql;

@Dao
public interface AchievementDao {
	@Sql("""
			CREATE TABLE IF NOT EXISTS achievements (
			    uuid CHAR(36) PRIMARY KEY NOT NULL,
			    name VARCHAR(255) NOT NULL,
			    description VARCHAR(255) NOT NULL,
			    type VARCHAR(255) NOT NULL,
			    points INT NOT NULL
			);
			""")
	@Script
	void createTable();
}
