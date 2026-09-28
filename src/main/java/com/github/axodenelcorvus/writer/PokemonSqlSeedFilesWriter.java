package com.github.axodenelcorvus.writer;

import com.github.axodenelcorvus.entry.PokemonEntry;
import com.github.axodenelcorvus.entry.PokemonSpriteEntry;
import com.github.axodenelcorvus.entry.SqlStringResolver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

//Supports writing and deleting file actions, based on user specified actions
public class PokemonSqlSeedFilesWriter {
    private Path directoryDestination;
    private final Path pokemonEntriesInsertionPath;
    private final Path pokemonSpriteEntriesInsertionPath;
    public static final String DEFAULT_DIRECTORY_DEST = "generated_sql_files";

    public PokemonSqlSeedFilesWriter(int generation) {
        this(generation, DEFAULT_DIRECTORY_DEST);
    }

    public PokemonSqlSeedFilesWriter(int generation, String directoryDestination) {
        this.setDirectoryDestination(directoryDestination);

        String basePokemonInsertFileSegment = "insert_gen%dpokemon".formatted(generation);
        pokemonEntriesInsertionPath = this.directoryDestination
                .resolve(Path.of(
                        basePokemonInsertFileSegment + getFileSuffix()
                ));

        String basePokemonSpriteInsertFileSegment = "insert_gen%d_significant_pokemon_sprites".formatted(generation);
        pokemonSpriteEntriesInsertionPath = this.directoryDestination
                .resolve(Path.of(
                        basePokemonSpriteInsertFileSegment + getFileSuffix()
                ));
    }

    private void setDirectoryDestination(String dirDestStr) {
        if (dirDestStr == null){
            this.directoryDestination = Path.of(DEFAULT_DIRECTORY_DEST);
            return;
        }

        this.directoryDestination = Path.of(dirDestStr).normalize();

        if (this.directoryDestination.isAbsolute())
            throw new InvalidPathException(dirDestStr, "Unexpected absolute path given");

        if (this.directoryDestination.getNameCount() != 1 || dirDestStr.startsWith(".."))
            throw new InvalidPathException(dirDestStr, "Only current directory or child directory is acceptable");
    }

    /**
     * Creates new directory as needed when directory does not exist yet.
     * In addition, to handle cases where specified directory is given, can reset destination directory to default one of this class and
     * create that directory if needed. This occurs when:
     * <li>Directory end-user specified exists but application does not have write privileges</li>
     * <li>Directory end-user specified exists as regular file with same name as specified directory</li>
     *
     * @return The path of a newly created directory, or none if directory already exists.
     */
    public Optional<Path> createDestinationDirectoryIfNotExists() throws IOException {
        if (Files.notExists(directoryDestination))
            return Optional.of(Files.createDirectory(directoryDestination));

        //These are expected to occur when specified directory is given by end user
        if ((!Files.isWritable(directoryDestination) || Files.isRegularFile(directoryDestination))) {
            directoryDestination = Path.of(DEFAULT_DIRECTORY_DEST);
            System.out.printf("INFO: Default directory %s was set for use %n", DEFAULT_DIRECTORY_DEST);

            if (Files.notExists(directoryDestination))
                return Optional.of(Files.createDirectory(directoryDestination));
        }

        return Optional.empty();
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

    public Path getPokemonEntriesInsertionPath() {
        return pokemonEntriesInsertionPath;
    }

    public Path getPokemonSpriteEntriesInsertionPath() {
        return pokemonSpriteEntriesInsertionPath;
    }

    /*
    This method makes it so this object writes to a new distinct file
    and does not overwrite pre-existing ones each time a method that
    writes to an SQL file is called.
     */
    private String getFileSuffix() {
        var currTime = LocalDateTime.now();
        var fileFriendlyDateTimeSegmentFormatter = DateTimeFormatter.ofPattern("_yy_MM-dd__nnnnnnnnn");
        String fileFriendlyDateTimeSegment = currTime.format(fileFriendlyDateTimeSegmentFormatter);

        return fileFriendlyDateTimeSegment + ".sql";
    }

    private String linesForInsertStatementFrom(String tableName, String columnList) {
        return "INSERT INTO " + tableName + '\n' +
            '\t' + columnList + "\nVALUES\n";
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