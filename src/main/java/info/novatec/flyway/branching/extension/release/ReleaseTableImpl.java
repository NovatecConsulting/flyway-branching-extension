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
package info.novatec.flyway.branching.extension.release;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.SqlScript;
import org.flywaydb.core.internal.dbsupport.Table;
import org.flywaydb.core.internal.util.PlaceholderReplacer;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.util.logging.LogFactory;
import org.flywaydb.core.internal.util.scanner.classpath.ClassPathResource;

/**
 * The release table implementation.
 */
public class ReleaseTableImpl implements ReleaseTable {
    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog(ReleaseTableImpl.class);

    /**
     * Database-specific functionality.
     */
    private final DbSupport dbSupport;

    /**
     * The release table used by flyway.
     */
    private final Table table;

    /**
     * JdbcTemplate with ddl manipulation access to the database.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * The default release.
     */
    private final String defaultRelease;

    /**
     * The ClassLoader to use.
     */
    private ClassLoader classLoader;

    /**
     * Creates a new instance of the release table support.
     *
     * @param dbSupport
     *            Database-specific functionality.
     * @param table
     *            The release table used by flyway.
     * @param defaultRelease
     *            Default release
     * @param classLoader
     *            The ClassLoader for loading migrations on the classpath.
     */
    public ReleaseTableImpl(final DbSupport dbSupport, final Table table,
            final String defaultRelease, final ClassLoader classLoader) {
        this.jdbcTemplate = dbSupport.getJdbcTemplate();
        this.dbSupport = dbSupport;
        this.table = table;
        this.defaultRelease = defaultRelease;
        this.classLoader = classLoader;

        createIfNotExists();
    }

    @Override
    public final void lock() {
        createIfNotExists();
        table.lock();
    }

    /**
     * Creates the release table if it doesn't exist.
     */
    private void createIfNotExists() {

        if (table.exists()) {
            LOG.debug(String.format(
                    "Release table '%s' already exists. No creation required",
                    table));
            return;
        }

        LOG.debug(String.format("Creating release table '%s'", table));

        String resourceName = "info/novatec/flyway/branching/extension/release/"
                + dbSupport.getDbName() + "/createReleaseTable.sql";
        String source = new ClassPathResource(resourceName, classLoader)
                .loadAsString("UTF-8");

        Map<String, String> placeholders = new HashMap<String, String>();
        placeholders.put("releaseVersionSchemaName", table.getSchema()
                .getName());
        placeholders.put("releaseVersionTableName", table.getName());
        placeholders.put("defaultRelease", defaultRelease);
        String sourceNoPlaceholders = new PlaceholderReplacer(placeholders,
                "${", "}").replacePlaceholders(source);

        SqlScript sqlScript = new SqlScript(sourceNoPlaceholders, dbSupport);
        sqlScript.execute(jdbcTemplate);

        LOG.info(String.format("Release table '%s' created.", table));
    }

    @Override
    public final String getCurrentRelease() {
        String currentRelease;

        createIfNotExists();

        try {
            currentRelease = jdbcTemplate.queryForString("select "
                    + dbSupport.quote("CURRENT_RELEASE") + " from " + table);
        } catch (SQLException e) {
            throw new FlywayException(
                    "Error while retrieving the current release "
                            + "from release table "
                            + table, e);
        }

        LOG.debug(String.format(
                "Got current release '%s' from release table '%s'",
                currentRelease, table));
        return currentRelease;
    }

    @Override
    public final void setCurrentRelease(final String release) {

        createIfNotExists();

        try {
            jdbcTemplate.update(
                    "update " + table + " set "
                            + dbSupport.quote("CURRENT_RELEASE") + " = ?",
                    release);
            LOG.debug(String.format(
                    "Set current release in release table '%s' to '%s'", table,
                    release));
        } catch (SQLException e) {
            throw new FlywayException(
                    "Error while updating the current release in release table "
                            + table, e);
        }
    }

    @Override
    public final String toString() {
        return table.toString();
    }

}
