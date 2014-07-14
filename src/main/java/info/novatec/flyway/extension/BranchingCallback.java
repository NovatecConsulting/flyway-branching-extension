package info.novatec.flyway.extension;

import info.novatec.flyway.extension.release.ReleaseTable;
import info.novatec.flyway.extension.release.ReleaseTableImpl;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.callback.FlywayCallback;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.util.jdbc.TransactionCallback;
import org.flywaydb.core.internal.util.jdbc.TransactionTemplate;
import org.flywaydb.core.internal.util.logging.Log;
import org.flywaydb.core.internal.util.logging.LogFactory;

/**
 * Callback for extending flywaydb for branching support.
 */
public class BranchingCallback implements FlywayCallback {
    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog(BranchingCallback.class);

    /**
     * The {@link Flyway} instance.
     */
    private Flyway flyway;

    /**
     * The {@link ReleaseTable} instance for release branches.
     */
    private ReleaseTable releaseTable;

    /**
     * The current active release.
     */
    private String currentRelease;

    /**
     * Creates a new instance of {@link BranchingCallback}.
     * @param flywayInstance
     *            the {@link Flyway} instance
     */
    public BranchingCallback(final Flyway flywayInstance) {
        super();
        this.flyway = flywayInstance;

        if (this.releaseTable == null) {
            try {
                this.releaseTable = initReleaseTable(
                        flywayInstance.getDataSource().getConnection());
            } catch (SQLException e) {
                throw new FlywayException(
                        "Error getting database connection for initializing "
                        + "release table. Reason: "
                                + ExceptionUtils.getRootCauseMessage(e));
            }
        }

        try {
            switchLocations(getCurrentRelease(flywayInstance.getDataSource()
                    .getConnection()));
        } catch (SQLException e) {
            throw new FlywayException(
                    "Error getting database connection for getting current "
                    + "release from release table. Reason: "
                            + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @Override
    public final void beforeClean(final Connection connection) {
        LOG.debug("before()");
    }

    @Override
    public final void afterClean(final Connection connection) {
        LOG.debug("afterClean()");
    }

    @Override
    public final void beforeMigrate(final Connection connection) {
        LOG.debug("beforeMigrate()");
    }

    @Override
    public final void afterMigrate(final Connection connection) {
        LOG.debug("afterMigrate()");

        if (this.releaseTable == null) {
                this.releaseTable = initReleaseTable(
                        connection);
        }

        String release = releaseTable.getCurrentRelease();
        if (!currentRelease.equalsIgnoreCase(release)) {
            switchLocations(release);
            int appliedMigrations = flyway.migrate();
            String currentVersion = getCurrentVersion();
            LOG.info(String.format(
                    "Successfully completed %s migration(s) to version %s",
                    appliedMigrations, currentVersion));
        }
    }

    @Override
    public final void beforeEachMigrate(final Connection connection,
            final MigrationInfo info) {
        LOG.debug(String.format("beforeEachMigrate(%s)",
                info.getDescription()));
    }

    @Override
    public final void afterEachMigrate(final Connection connection,
            final MigrationInfo info) {
        LOG.debug(String.format("afterEachMigrate(%s)", info.getDescription()));
    }

    @Override
    public final void beforeValidate(final Connection connection) {
        LOG.debug("beforeValidate()");
    }

    @Override
    public final void afterValidate(final Connection connection) {
        LOG.debug("afterValidate()");
    }

    @Override
    public final void beforeInit(final Connection connection) {
        LOG.debug("beforeInit()");
    }

    @Override
    public final void afterInit(final Connection connection) {
        LOG.debug("afterInit()");
    }

    @Override
    public final void beforeRepair(final Connection connection) {
        LOG.debug("beforeRepair()");
    }

    @Override
    public final void afterRepair(final Connection connection) {
        LOG.debug("afterRepair()");
    }

    @Override
    public final void beforeInfo(final Connection connection) {
        LOG.debug("beforeInfo()");
    }

    @Override
    public final void afterInfo(final Connection connection) {
        LOG.debug("afterInfo()");
    }

    /**
     * Initializes the release table, i.e. creates it if it is not yet existing.
     * @param connection the jdbc connection
     * @return the initialized {@link ReleaseTable} instance
     */
    private ReleaseTable initReleaseTable(final Connection connection) {
        final DbSupport dbSupport = DbSupportFactory.createDbSupport(connection, false);
        final Schema currentSchema = dbSupport.getCurrentSchema();

        return new TransactionTemplate(connection)
                .execute(new TransactionCallback<ReleaseTable>() {
                    public ReleaseTable doInTransaction() {
                        return new ReleaseTableImpl(dbSupport, currentSchema.getTable("releasetable"), "main",
                                this.getClass().getClassLoader());
                    }
                });
    }

    /**
     * Switches locations for given release.
     * @param newRelease the new release to set for locations
     */
    private void switchLocations(final String newRelease) {

        String[] locations = this.flyway.getLocations();
        String[] newLocations = new String[locations.length];
        String newLocation;
        for (int i = 0; i < locations.length; i++) {
            if (StringUtils.isNotBlank(this.currentRelease)
                    && locations[i].endsWith(currentRelease)) {
                newLocation = locations[i].substring(0,
                        locations[i].lastIndexOf('/'));
            } else {
                newLocation = locations[i];
            }
            newLocations[i] = newLocation + "/" + newRelease;
        }

        LOG.debug(String.format("Set locations to %s",
                StringUtils.join(newLocations, ",")));
        this.flyway.setLocations(newLocations);
        this.currentRelease = newRelease;
    }

    /**
     * Gets the current version for latest migration.
     * @return the current version
     */
    private String getCurrentVersion() {
        String currentVersion = null;
        MigrationInfoService migrationInfoService = flyway.info();
        if (migrationInfoService.current() != null
                && migrationInfoService.current().getVersion() != null) {
            currentVersion = migrationInfoService.current().getVersion()
                    .getVersion();
        }
        return currentVersion;
    }

    /**
     * Gets the current active release.
     * @param connection jdbc connection
     * @return the current release
     */
    private String getCurrentRelease(final Connection connection) {
        if (this.releaseTable == null) {
            this.releaseTable = initReleaseTable(
                    connection);
        }

        return new TransactionTemplate(connection)
                .execute(new TransactionCallback<String>() {
                    public String doInTransaction() {
                        return releaseTable.getCurrentRelease();
                    }
                });
    }
}
