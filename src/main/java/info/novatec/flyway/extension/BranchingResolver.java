package info.novatec.flyway.extension;

import java.util.Collection;

import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.api.resolver.ResolvedMigration;

/**
 * Migration resolver for custom artifacts for release change.
 */
public class BranchingResolver implements MigrationResolver {

    @Override
    public final Collection<ResolvedMigration> resolveMigrations() {
        // TODO Auto-generated method stub
        return null;
    }

}
