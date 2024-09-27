package org.mintdaniel42.starediscordbot.data;

import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import org.mintdaniel42.starediscordbot.BotConfig;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.dialect.Dialect;
import org.seasar.doma.jdbc.dialect.SqliteDialect;
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource;
import org.seasar.doma.jdbc.tx.LocalTransactionManager;
import org.seasar.doma.jdbc.tx.TransactionManager;

@Getter
@Singleton
public final class DatabaseConfig implements Config, AutoCloseable {
	@NonNull private final Dialect dialect;
	@NonNull private final LocalTransactionDataSource dataSource;
	@NonNull private final TransactionManager transactionManager;

	public DatabaseConfig(@NonNull final BotConfig config) {
		// TODO switch between dialects
		dialect = new SqliteDialect();
		dataSource = new LocalTransactionDataSource(config.getJdbcUrl(), null, null);
		transactionManager = new LocalTransactionManager(dataSource.getLocalTransaction(getJdbcLogger()));
	}

	@Override
	public void close() throws Exception {
		dataSource.getConnection().close();
	}
}
