package config;

import kg.biamino.projects.config.DatabaseHealthIndicator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatabaseHealthIndicatorTest {

    @Test
    void health_validConnection_returnsUp() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(3)).thenReturn(true);

        Health health = new DatabaseHealthIndicator(dataSource).health();

        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    void health_invalidConnection_returnsDown() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(3)).thenReturn(false);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getDatabaseProductName()).thenReturn("PostgreSQL");

        Health health = new DatabaseHealthIndicator(dataSource).health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("PostgreSQL", health.getDetails().get("database"));
    }

    @Test
    void health_connectionThrows_returnsDown() throws SQLException {
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenThrow(new SQLException("connection refused"));

        Health health = new DatabaseHealthIndicator(dataSource).health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}
