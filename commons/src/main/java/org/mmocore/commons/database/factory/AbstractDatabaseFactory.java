package org.mmocore.commons.database.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Java-man
 */
public abstract class AbstractDatabaseFactory {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private HikariDataSource connectionPool;

    public void doStart() {
        try {
            final String configPath = getConfigPath();
            final HikariConfig config = new HikariConfig(configPath);
            connectionPool = new HikariDataSource(config);

            logger.info("Database connection working.");
        } catch (final RuntimeException e) {
            logger.warn("Could not init database connection.", e);
        }
    }

    public void doStop() {
        connectionPool.shutdown();
    }

    public Connection getConnection() {
        try {
            return connectionPool.getConnection();
        } catch (final SQLException e) {
            logger.warn("Can't get connection from database", e);
        }

        return null;
    }

    public HikariDataSource getConnectionPool() {
        return connectionPool;
    }

    protected abstract String getConfigPath();

    /**
     * Return total number of connections currently in use by an application.
     *
     * @return no of leased connections
     */
    public int getTotalLeased() {
        return 0;
        // return connectionPool.getTotalLeased();
    }

    /**
     * Return the number of free connections available to an application right away (excluding connections that can be created dynamically).
     *
     * @return number of free connections
     */
    public int getTotalFree() {
        return 0;
        // return connectionPool.getTotalFree();
    }

    /**
     * Return total number of connections created in all partitions.
     *
     * @return number of created connections
     */
    public int getTotalCreatedConnections() {
        return 0;
        // return connectionPool.getTotalCreatedConnections();
    }
}
