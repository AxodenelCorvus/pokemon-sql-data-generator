package com.github.axodenelcorvus.writer;

import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.regex.Pattern;

public class PokemonSqlSeedFilesRemover extends SimpleFileVisitor<Path> {

    private int removalCount = 0;
    private final SqlSeedFilesContext filesContext;
    private final Pattern prefixPatternTargeted;

    public PokemonSqlSeedFilesRemover(
            int removeGenerationStartFrom,
            int removeGenerationToInclusiveEnd,
            Path directoryDestination
    ) {
        this.filesContext = new SqlSeedFilesContext(directoryDestination);

        String prefixPatternGenerationToTarget =
                SqlSeedFilesContext.SQL_FILE_PREFIX_REMOVAL_TARGET_PATTERN
                    .formatted(removeGenerationStartFrom, removeGenerationToInclusiveEnd);

        prefixPatternTargeted = Pattern.compile(prefixPatternGenerationToTarget);
    }

}
