package com.github.axodenelcorvus.writer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

class GeneratedSqlSeedFilesRemover extends SimpleFileVisitor<Path> {
    private int removalCount = 0;

    public int getRemovalCount() { return removalCount; }

    @Override
    public FileVisitResult visitFile(Path fileCandidate,
                                     BasicFileAttributes fileAttrs) throws IOException {

        String fileCandidateName = fileCandidate.getFileName().toString();

        //Go ahead and silently skip any files without correct extension for some safety
        if (!fileCandidateName.endsWith(".sql"))
            return FileVisitResult.CONTINUE;

        Files.delete(fileCandidate);
        removalCount++;

        return FileVisitResult.CONTINUE;
    }
}