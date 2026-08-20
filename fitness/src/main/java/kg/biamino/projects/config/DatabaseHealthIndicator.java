package kg.biamino.projects.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Health health() {
        try(Connection connection = dataSource.getConnection()) {
            if(connection.isValid(3)) {
                return Health.up()
                        .withDetail("database","valid number of connections")
                        .build();
            }else {
                return Health.down()
                        .withDetail("database", connection.getMetaData().getDatabaseProductName())
                        .build();
            }
        }catch (SQLException e){
            return Health.down(e).build();
        }
    }

}
