package org.mintdaniel42.starediscordbot.data.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Script;
import org.seasar.doma.Sql;

@Dao
public interface RequestDao {
	@Sql("""
			CREATE TABLE IF NOT EXISTS requests (
			    timestamp BIGINT PRIMARY KEY NOT NULL,
			    uuid CHAR(36),
			    rating VARCHAR(255),
			    joined VARCHAR(255),
			    points BIGINT NOT NULL,
			    luck DOUBLE NOT NULL,
			    quota DOUBLE NOT NULL,
			    winrate DOUBLE NOT NULL,
			    secondary BOOLEAN NOT NULL,
			    banned BOOLEAN NOT NULL,
			    cheating BOOLEAN NOT NULL,
			    tag VARCHAR(255),
			    name VARCHAR(255),
			    leader CHAR(36),
			    relation VARCHAR(255),
			    group_id VARCHAR(255),
			    discord BIGINT NOT NULL,
			    note VARCHAR(255),
			    top10 VARCHAR(255),
			    streak INT NOT NULL,
			    highestRank VARCHAR(255),
			    database VARCHAR(255) NOT NULL
			);
			""")
	@Script
	void createTable();

	@Sql("""
			ALTER TABLE requests
			RENAME COLUMN group_id TO groupTag
			""")
	@Script
	void renameColumnGroupTag();

	@Sql("""
			ALTER TABLE requests
			RENAME COLUMN database TO type
			""")
	@Script
	void renameColumnType();
}
