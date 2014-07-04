package info.novatec.flyway.extension;

import java.sql.Connection;
import java.sql.SQLException;

import org.flywaydb.core.api.resolver.MigrationExecutor;

/**
 * Migration executor for custom artifacts for release change.
 *
 */
public class BranchingExecutor implements MigrationExecutor {

    @Override
    public void execute(final Connection connection) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public final boolean executeInTransaction() {
        // TODO Auto-generated method stub
        return false;
    }

}
