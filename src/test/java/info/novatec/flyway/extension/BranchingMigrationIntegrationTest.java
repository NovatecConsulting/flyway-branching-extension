package info.novatec.flyway.extension;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfoService;
import org.junit.After;
import org.junit.Test;

/**
 * Integration test to verify the branching support for flywaydb migrations.
 */
public class BranchingMigrationIntegrationTest {
    private static final int EXPECTED_MIGRATIONS = 3;
    private static final long SLEEP_TIME = 1000L;
    private String databasePath;

    /**
     * Verifies that db migrations for branches is working.
     */
    @Test
    public final void verifyBranchingMigrations() {
        // Create the Flyway instance
        Flyway cut = new Flyway();

        // Point it to the database
        databasePath = "dbmigration" + System.nanoTime();
        cut.setDataSource("jdbc:h2:file:./target/" + databasePath, "sa", null);

        cut.setLocations("db/branching/migrations");
        cut.setCallbacks(new BranchingCallback(cut));
        cut.setValidateOnMigrate(false);
        cut.setInitOnMigrate(true);
        cut.setInitVersion("0");

        // Start the migration
        int migrations = cut.migrate();

        assertThat("Performed expected number of migrations",
                migrations, is(EXPECTED_MIGRATIONS));

        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            // Not expected
        }

        MigrationInfoService migrationInfoService = cut.info();
        assertThat("No more pending migrations should be avilable",
                migrationInfoService.pending().length, is(0));
    }

    /**
     * Cleaning up the test database.
     * @throws InterruptedException if thread is interrupted
     */
    @After
    public final void cleanup() throws InterruptedException {
        Thread.sleep(SLEEP_TIME);
        File file = new File("./target/" + databasePath);
        if (file.exists()) {
            FileUtils.deleteQuietly(file);
        }
    }

}
