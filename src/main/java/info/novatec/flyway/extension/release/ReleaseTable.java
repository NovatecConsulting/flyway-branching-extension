package info.novatec.flyway.extension.release;

/**
 * Release table for branching extension.
 */
public interface ReleaseTable {
    /**
     * Acquires an exclusive read-write lock on the release table. This lock
     * will be released automatically on commit.
     */
    void lock();

    /**
     * Gets the current release from release table.
     * @return the current release
     */
    String getCurrentRelease();

    /**
     * Sets current release in release table.
     * @param release
     *            the new release to set
     */
    void setCurrentRelease(String release);

}
