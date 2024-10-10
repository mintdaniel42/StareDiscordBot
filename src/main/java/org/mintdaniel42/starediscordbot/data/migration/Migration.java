package org.mintdaniel42.starediscordbot.data.migration;

@FunctionalInterface
public interface Migration {
	/**
	 * Perform a migration for the current version and bump the database to a higher version
	 *
	 * @param version the current version
	 * @return the new version (may also skip versions)
	 */
	int apply(final int version);
}
