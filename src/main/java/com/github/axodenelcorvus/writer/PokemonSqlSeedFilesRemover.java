package com.github.axodenelcorvus.writer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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

    public void removeAllTargetedSqlSeedFiles() throws IOException {
        Files.walkFileTree(filesContext.getDirectoryDestination(), this);
    }

    public int getRemovalCount() { return removalCount; }

    public Pattern getPrefixPatternTargeted() {
        return prefixPatternTargeted;
    }

    public Path getTargetedDirectory() {
        return filesContext.getTargetDestination();
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir,
                                             BasicFileAttributes basicFileAttributes) {
        if (dir.equals(filesContext.getDirectoryDestination()))
            return FileVisitResult.CONTINUE;
        else  //Silently skip any nested directories,
            return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    //Note it is possible no files are removed when given wrong directory if prefix and extension not met
    public FileVisitResult visitFile(Path fileCandidate,
                                     BasicFileAttributes fileAttrs) throws IOException {

        String fileCandidateName = fileCandidate.getFileName().toString();

        //Check extension 1st
        if (fileCandidateName.endsWith(".sql") && fileCandidateName.matches(prefixPatternTargeted.pattern())){
            Files.delete(fileCandidate);
            removalCount++;
        }

        return FileVisitResult.CONTINUE;
    }

}
