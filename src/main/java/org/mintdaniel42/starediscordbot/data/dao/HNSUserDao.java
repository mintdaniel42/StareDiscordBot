package org.mintdaniel42.starediscordbot.data.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Script;
import org.seasar.doma.Sql;

@Dao
public interface HNSUserDao {
	@Sql("""
			CREATE TABLE IF NOT EXISTS hns_entries (
			    uuid CHAR(36) PRIMARY KEY NOT NULL,
			    rating VARCHAR(255) NOT NULL,
			    joined VARCHAR(255) NOT NULL,
			    points BIGINT NOT NULL,
			    secondary BOOLEAN NOT NULL,
			    banned BOOLEAN NOT NULL,
			    cheating BOOLEAN NOT NULL,
			    top10 VARCHAR(255) NOT NULL,
			    streak INT NOT NULL,
			    highestRank VARCHAR(255) NOT NULL
			);
			""")
	@Script
	void createTable();
}
