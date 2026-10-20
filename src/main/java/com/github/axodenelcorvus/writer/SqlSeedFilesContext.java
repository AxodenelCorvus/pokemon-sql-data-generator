package com.github.axodenelcorvus.writer;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final class SqlSeedFilesContext {
    private Path directoryTargeted;

    public static final Path DEFAULT_DIRECTORY_DEST = Path.of("generated_sql_files");

    public static final String SQL_FILE_PREFIX_FORMAT = "insert_gen%d_";
    public static final String SQL_FILE_PREFIX_REMOVAL_TARGET_PATTERN = "^insert_gen[%d-%d]_";

    public SqlSeedFilesContext(Path dirTargeted) {
        //TODO Review if null check defaulting behavior needed later
        if (dirTargeted == null) {
            this.directoryTargeted = DEFAULT_DIRECTORY_DEST;
            return;
        }

        Path normalizedDirDestination = dirTargeted.normalize();

        if (normalizedDirDestination.isAbsolute())
            throw new InvalidPathException(dirTargeted.toString(), "Unexpected absolute path given");

        if (normalizedDirDestination.getNameCount() != 1 || normalizedDirDestination.startsWith(".."))
            throw new InvalidPathException(dirTargeted.toString(), "Only current working directory or a child directory of it is acceptable");

        this.directoryTargeted = normalizedDirDestination;
    }

    /*
    This method makes it so writes occur to a new distinct SQL file for SQL files created per each call to generator command
    and do not overwrite existing ones. This is due to nanoseconds block at end of suffix.
    */
    public static String provideFileSuffixUsing(LocalDateTime currTime) {
        var fileFriendlyDateTimeSuffixFormatter = DateTimeFormatter.ofPattern("_yy-MM-dd__nnnnnnnnn");
        return currTime.format(fileFriendlyDateTimeSuffixFormatter) + ".sql";
    }

    public void updateDirDestinationToDefault() {
        directoryTargeted = DEFAULT_DIRECTORY_DEST;
    }


    public Path getTargetDestination() {
        return directoryTargeted;
    }

    public Path getDirectoryDestination() {
        return this.getTargetDestination();
    }

}
