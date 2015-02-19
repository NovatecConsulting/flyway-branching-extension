/*
* Flyway Branching Extension.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package info.novatec.flyway.branching.extension;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfoService;
import org.junit.Test;

/**
 * Integration test to verify the branching support for flywaydb migrations.
 */
public abstract class AbstractBranchingMigrationIntegrationTest {
    protected static final long SLEEP_TIME = 1000L;

    private static final int EXPECTED_MIGRATIONS = 3;

    /**
     * Verifies that db migrations for branches is working.
     */
    @Test
    public final void verifyBranchingMigrations() {
        // Create the Flyway instance
        Flyway cut = new Flyway();

        // Point it to the database
        cut.setDataSource(getJdbcUrl(), getUserName(), getPassword());

        cut.setLocations(getLocations());
        cut.setCallbacks(new BranchingCallback(cut));
        cut.setValidateOnMigrate(false);
        cut.setBaselineOnMigrate ( true );
        cut.setBaselineVersion("0");

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
    
    protected abstract String getJdbcUrl();
    
    protected abstract String getUserName();
    
    protected abstract String getPassword();
    
    protected abstract String[] getLocations();
    
    protected abstract DataSource getDatasource();

}
