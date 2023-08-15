package com.tasky.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * A class containing test cases to verify the functionality of the PostgreSQL container setup and configuration
 * using the {@link AbstractTestContainer} as a base class.
 */
public class TestContainerTest extends AbstractTestContainer {

    /**
     * Verifies that the PostgreSQL database container starts successfully and is in a running state.
     * Also checks if the container has been created.
     */
    @Test
    void canStartPostgresDb() {
        Assertions.assertThat(postgreSQLContainer.isRunning()).isTrue();
        Assertions.assertThat(postgreSQLContainer.isCreated()).isTrue();
    }
}
