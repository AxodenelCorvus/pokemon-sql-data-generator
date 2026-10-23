package com.github.axodenelcorvus.writer;

import com.github.axodenelcorvus.model.entry.PokemonEntry;
import com.github.axodenelcorvus.model.entry.PokemonSpriteEntry;
import com.github.axodenelcorvus.model.entry.SqlStringResolver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PokemonSqlSeedFilesWriter {
    private final SqlSeedFilesContext filesContext;
    private Path pokemonEntriesInsertionPath;
    private Path pokemonSpriteEntriesInsertionPath;

    private final int generation;

    public PokemonSqlSeedFilesWriter(int generation) {
        this(generation, SqlSeedFilesContext.DEFAULT_DIRECTORY_DEST);
    }

    public PokemonSqlSeedFilesWriter(int generation, Path directoryDestination) {
        this.filesContext = new SqlSeedFilesContext(directoryDestination);
        this.generation = generation;

        String sharedSuffix = SqlSeedFilesContext.provideFileSuffixUsing(LocalDateTime.now());

        Path dirDest = filesContext.getDirectoryDestination();

        pokemonEntriesInsertionPath = dirDest.resolve(
                Path.of(buildSqlInsertionFileName(sharedSuffix, "_pokemon"))
        );

        pokemonSpriteEntriesInsertionPath = dirDest.resolve(
                Path.of(buildSqlInsertionFileName(sharedSuffix, "_significant_pokemon_sprites"))
        );
    }

    private String buildSqlInsertionFileName(String suffix, String body) {
        return "%s%s%s".formatted(
                SqlSeedFilesContext.SQL_FILE_PREFIX_FORMAT.formatted(generation),
                body,
                suffix
        );
    }

    public static Path getDefaultDirDestination() {
        return SqlSeedFilesContext.DEFAULT_DIRECTORY_DEST;
    }

    private void resetWithDefaultDirectoryState() {
        this.filesContext.updateDirDestinationToDefault();

        Path currPokemonInsertFile = pokemonEntriesInsertionPath.getFileName();
        this.pokemonEntriesInsertionPath = filesContext.getDirectoryDestination().resolve(currPokemonInsertFile);
        Path currPokemonSpritesInsertFile = pokemonSpriteEntriesInsertionPath.getFileName();
        this.pokemonSpriteEntriesInsertionPath =  filesContext.getDirectoryDestination().resolve(currPokemonSpritesInsertFile);
    }

    /**
     * Creates new directory as needed when directory does not exist yet.
     * In addition, to handle cases where specified directory is given, can reset destination directory to default one and
     * create that directory if needed. This occurs when:
     * <li>Directory end-user specified exists but application does not have write privileges</li>
     * <li>Directory end-user specified exists as regular file with same name as specified directory</li>
     *
     * @return The path of a newly created directory, or none if directory already exists.
     */
    public Optional<Path> createDestinationDirectoryIfNotExists() throws IOException {
        Path directoryDestination = filesContext.getDirectoryDestination();

        if (Files.notExists(directoryDestination))
            return Optional.of(Files.createDirectory(directoryDestination));

        //These are expected to occur when specified directory is given by end user
        if ((!Files.isWritable(directoryDestination) || Files.isRegularFile(directoryDestination))) {
            this.resetWithDefaultDirectoryState();

            //Will need to regrab updated directory from context
            Path updatedDirectoryDestination = filesContext.getDirectoryDestination();

            if (Files.notExists(updatedDirectoryDestination))
                return Optional.of(Files.createDirectory(updatedDirectoryDestination));
        }
        return Optional.empty();
    }

    public Path getPokemonEntriesInsertionPath() {
        return pokemonEntriesInsertionPath;
    }

    public Path getPokemonSpriteEntriesInsertionPath() {
        return pokemonSpriteEntriesInsertionPath;
    }

    private String linesForInsertStatementFrom(String tableName, String columnList) {
        return "INSERT INTO " + tableName + '\n' +
            '\t' + columnList + '\n' +
            "VALUES\n";
    }

    public void writeSpriteRowEntries(List<PokemonSpriteEntry> pokemonSpriteEntries) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(pokemonSpriteEntriesInsertionPath)) {
            SqlStringResolver usingStrLiteralResolver = new SqlStringResolver();
            String tupleToEnter;
            String lastTupleEntry = '\t' + pokemonSpriteEntries.getLast().toSqlTuple(usingStrLiteralResolver) + ';';

            writer.write(linesForInsertStatementFrom(PokemonSpriteEntry.TABLE_NAME, PokemonSpriteEntry.COLUMN_LIST));

            for (PokemonSpriteEntry entry : pokemonSpriteEntries.subList(0, pokemonSpriteEntries.size() - 1)) {
                tupleToEnter = '\t' + entry.toSqlTuple(usingStrLiteralResolver) + ",\n";
                writer.write(tupleToEnter);
            }

            writer.write(lastTupleEntry);
        }
    }

    public void writePokemonRowEntries(List<PokemonEntry> pokemonEntries) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(pokemonEntriesInsertionPath)) {
            SqlStringResolver usingStrLiteralResolver = new SqlStringResolver();
            String tupleToEnter;
            String lastTupleEntry = '\t' + pokemonEntries.getLast().toSqlTuple(usingStrLiteralResolver) + ';';

            writer.write(linesForInsertStatementFrom(PokemonEntry.TABLE_NAME, PokemonEntry.COLUMN_LIST));

            for (PokemonEntry entry : pokemonEntries.subList(0, pokemonEntries.size() - 1)) {
                tupleToEnter = '\t' + entry.toSqlTuple(usingStrLiteralResolver) + ",\n";
                writer.write(tupleToEnter);
            }

            writer.write(lastTupleEntry);
        }
    }

}