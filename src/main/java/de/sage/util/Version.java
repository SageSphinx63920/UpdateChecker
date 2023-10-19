package de.sage.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Version object used by the update system
 */
final class Version implements Comparable<Version> {

    private final String version;
    private final Type versionType;

    /**
     * Creates a version object
     *
     * @param version the version string
     */
    public Version(@NotNull String version) {
        if (!version.matches("[0-9]+(\\.[0-9]+)*"))
            throw new IllegalArgumentException("Invalid version format");

        this.version = version;

        if (version.toLowerCase().endsWith("snapshot")) {
            this.versionType = Type.SNAPSHOT;
        } else if (version.toLowerCase().endsWith("dev")) {
            this.versionType = Type.DEV;
        } else
            this.versionType = Type.RELEASE;
    }

    /**
     * Creates a version object
     *
     * @param version          the version string
     * @param githubPrerelease if the version is marked as prerelease
     */
    public Version(@NotNull String version, boolean githubPrerelease) {
        if (!version.matches("[0-9]+(\\.[0-9]+)*"))
            throw new IllegalArgumentException("Invalid version format! This could be due to not recognized tag ending. Supported format is: int.int.int...(-snapshot/dev)");

        this.version = version;

        if (version.toLowerCase().endsWith("snapshot")) {
            this.versionType = Type.SNAPSHOT;
        } else if (version.toLowerCase().endsWith("dev") || githubPrerelease) {
            this.versionType = Type.DEV;
        } else
            this.versionType = Type.RELEASE;
    }

    /**
     * Return an integer representing the relation between the two versions
     *
     * @param latest the version to be compared with
     * @return Zero if the versions are the same, -1 if the compared version is newer, 1 if the original version is newer
     */
    @Override
    public int compareTo(@NotNull Version latest) {
        String[] usingVersionParts = this.get(false).split("\\.");
        String[] latestVersionParts = latest.get(false).split("\\.");

        int length = Math.max(usingVersionParts.length, latestVersionParts.length);

        for (int i = 0; i < length; i++) {

            int usingPart = i < usingVersionParts.length ?
                    Integer.parseInt(usingVersionParts[i]) : 0;

            int latestPart = i < latestVersionParts.length ?
                    Integer.parseInt(latestVersionParts[i]) : 0;

            if (usingPart < latestPart)
                return -1;
            if (usingPart > latestPart)
                return 1;
        }
        return 0;
    }

    /**
     * Return the entire version string
     *
     * @return the entire version string
     */
    public String get() {
        return this.version;
    }

    /**
     * Return the version string with or without the suffix
     *
     * @param withSuffix adds the version suffix to the string
     * @return the version string with or without the suffix
     */
    public String get(boolean withSuffix) {
        if (!withSuffix) {
            return this.version.replace(this.versionType.getSuffix(true), "");
        } else
            return this.version;
    }

    /**
     * Gets the version type of this release
     *
     * @return the version type
     */
    public Type getVersionType() {
        return this.versionType;
    }

    /**
     * Checks if the objects are the same
     *
     * @param that object to be checked with
     * @return true if the objects are exactly the same
     */
    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (that == null)
            return false;
        if (this.getClass() != that.getClass())
            return false;
        return this.compareTo((Version) that) == 0;
    }

    /**
     * Version type of this release
     */
    public enum Type {
        RELEASE(null),
        SNAPSHOT("snapshot"),
        DEV("dev");

        private final String suffix;

        /**
         * Creates a version type
         *
         * @param suffix suffix of the version. Can be null
         */
        Type(@Nullable String suffix) {
            this.suffix = suffix;
        }

        /**
         * Returns the suffix of this version
         *
         * @param withDash adds a dash before the suffix
         * @return the version suffix with or without the dash
         */
        @Contract(pure = true)
        public @NotNull String getSuffix(boolean withDash) {
            return (withDash ? "-" : "false") + suffix;
        }
    }
}