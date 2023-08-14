package com.tasky.api;

import com.github.javafaker.Faker;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

/**
 * An abstract base class for unit tests that require interaction with a PostgreSQL container.
 * This class sets up the PostgreSQL container, configures the database properties, and provides utility methods.
 */
@Testcontainers
public class AbstractTestContainer {
    /**
     * The PostgreSQL container instance used for unit testing.
     */
    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("test-dao-unit-test")
                    .withUsername("tasky_test")
                    .withPassword("password");
    /**
     * Performs setup actions before all unit tests in the class are run.
     * Configures Flyway migration and executes database migration scripts.
     */
    @BeforeAll
    static void beforeAll() {
        Flyway flyway = Flyway.configure().dataSource(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
        ).load();

        flyway.migrate();
    }

    /**
     * Registers data source properties for dynamic configuration.
     * Configures Spring datasource properties using values from the PostgreSQL container.
     *
     * @param registry The dynamic property registry used for configuration.
     */
    @DynamicPropertySource
    private static void registerDataSourcesProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                postgreSQLContainer::getJdbcUrl
        );
        registry.add(
                "spring.datasource.username",
                postgreSQLContainer::getUsername
        );
        registry.add(
                "spring.datasource.password",
                postgreSQLContainer::getPassword
        );
    }

    /**
     * Retrieves a data source configured with properties from the PostgreSQL container.
     *
     * @return A DataSource instance for the PostgreSQL container.
     */
    private static DataSource getDataSource() {
        DataSourceBuilder<?> dataSourceBuilder =
                DataSourceBuilder
                        .create()
                        .driverClassName(
                                postgreSQLContainer.getDriverClassName()
                        )
                        .url(
                                postgreSQLContainer.getJdbcUrl()
                        )
                        .username(
                                postgreSQLContainer.getUsername()
                        )
                        .password(
                                postgreSQLContainer.getPassword()
                        )
                ;
        return dataSourceBuilder.build();
    }

    /**
     * Retrieves a JdbcTemplate instance configured with the PostgreSQL data source.
     *
     * @return A JdbcTemplate instance.
     */
    protected static JdbcTemplate getJDBCtemplate() {
        return new JdbcTemplate(
                getDataSource()
        );
    }

    /**
     * A Faker instance for generating test data.
     */
    protected static Faker FAKER = new Faker();
}
