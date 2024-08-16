package org.mintdaniel42.starediscordbot.data;

import lombok.Getter;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.SqliteDialect;
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource;
import org.seasar.doma.jdbc.tx.LocalTransactionManager;
import org.seasar.doma.jdbc.tx.TransactionManager;

@Getter
public final class DatabaseConfig implements Config {
	@NonNull private final Dialect dialect;
	@NonNull private final LocalTransactionDataSource dataSource;
	@NonNull private final TransactionManager transactionManager;

	public DatabaseConfig() {
		// TODO switch between dialects
		dialect = new SqliteDialect();
		dataSource = new LocalTransactionDataSource(Options.getJdbcUrl(), null, null);
		transactionManager = new LocalTransactionManager(dataSource.getLocalTransaction(getJdbcLogger()));
	}
}
