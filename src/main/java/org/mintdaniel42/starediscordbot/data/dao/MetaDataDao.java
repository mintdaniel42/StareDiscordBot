package org.mintdaniel42.starediscordbot.data.dao;

import org.seasar.doma.Dao;
import org.seasar.doma.Script;
import org.seasar.doma.Sql;

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
}
