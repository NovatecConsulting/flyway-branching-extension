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

import java.io.File;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.junit.After;

/**
 * Integration test to verify the branching support for flywaydb migrations.
 */
public class BranchingMigrationH2IntegrationTest extends AbstractBranchingMigrationIntegrationTest {

    private String databasePath;

    @Override
    protected String getJdbcUrl() {
    	databasePath = "dbmigration" + System.nanoTime();
    	return "jdbc:h2:file:./target/" + databasePath;
    }
    
    @Override
    protected String getUserName() {
    	return "sa";
    }
    
    @Override
    protected String getPassword() {
    	return null;
    }
    
    @Override
    protected String[] getLocations() {
    	return new String[] {"db/branching/migrations/h2"};
    }
    
    @Override
    protected DataSource getDatasource() {
    	return null;
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
