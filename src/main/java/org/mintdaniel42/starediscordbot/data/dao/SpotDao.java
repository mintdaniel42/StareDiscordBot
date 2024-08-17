package org.mintdaniel42.starediscordbot.data.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Script;
import org.seasar.doma.Sql;

@Dao
public interface SpotDao {
	@Sql("""
			CREATE TABLE spots (
			    uuid CHAR(36) PRIMARY KEY NOT NULL,
			    mapUUID CHAR(36) NOT NULL,
			    finderUUID CHAR(36),
			    blockId VARCHAR(255) NOT NULL,
			    rating VARCHAR(255) NOT NULL,
			    videolink VARCHAR(255) NOT NULL,
			    type VARCHAR(255) NOT NULL,
			    twoPlayers BOOLEAN NOT NULL
			);
			""")
	@Script
	void createTable();
}
