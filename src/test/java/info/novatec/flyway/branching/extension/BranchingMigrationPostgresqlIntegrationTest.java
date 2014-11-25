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

import javax.sql.DataSource;

import org.junit.After;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Integration test to verify the branching support for flywaydb migrations.
 */
public class BranchingMigrationPostgresqlIntegrationTest extends AbstractBranchingMigrationIntegrationTest {

    @Override
    protected String getJdbcUrl() {
    	return "jdbc:postgresql:dbmigration";
    }
    
    @Override
    protected String getUserName() {
    	return "dbmigration";
    }
    
    @Override
    protected String getPassword() {
    	return "dbmigration";
    }
    
    @Override
    protected String[] getLocations() {
    	return new String[] {"db/branching/migrations/postgresql"};
    }    
    
    @Override
    protected DataSource getDatasource() {
    	DriverManagerDataSource dataSource = new DriverManagerDataSource(getJdbcUrl(), getUserName(), getPassword());
    	dataSource.setDriverClassName("org.postgresql.Driver");
    	return dataSource;
    }
    
    /**
     * Cleaning up the test database.
     * @throws InterruptedException if thread is interrupted
     */
	@After
	public final void cleanup() throws InterruptedException {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(getDatasource());
		try {
			jdbcTemplate.update("drop table person");
		} catch (DataAccessException e) {
			// No OP
		}
		try {
			jdbcTemplate.update("drop table releasetable");
		} catch (DataAccessException e) {
			// No OP
		}
		try {
			jdbcTemplate.update("drop table schema_version");
		} catch (DataAccessException e) {
			// No OP
		}
	}

}
