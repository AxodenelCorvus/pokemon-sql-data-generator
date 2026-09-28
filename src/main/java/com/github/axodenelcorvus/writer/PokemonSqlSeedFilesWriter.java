package com.github.axodenelcorvus.writer;

import com.github.axodenelcorvus.entry.PokemonEntry;
import com.github.axodenelcorvus.entry.PokemonSpriteEntry;
import com.github.axodenelcorvus.entry.SqlStringResolver;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

//Supports writing and deleting file actions, based on user specified actions
public class PokemonSqlSeedFilesWriter {
    private Path directoryDestination;
    private final Path pokemonEntriesInsertionFile;
    private final Path pokemonSpriteEntriesInsertionFile;
    private static final String DEFAULT_DIR_USED = "generated_sql_files";

    public PokemonSqlSeedFilesWriter(int generation) {
        this(generation, DEFAULT_DIR_USED);
    }

    public PokemonSqlSeedFilesWriter(int generation, String directoryDestination) {
        if (directoryDestination == null)
            directoryDestination = DEFAULT_DIR_USED;

        //TODO Validate directory destination here
        this.directoryDestination = Path.of(directoryDestination);

        String basePokemonInsertFileSegment = "insert_gen%dpokemon".formatted(generation);
        pokemonEntriesInsertionFile = this.directoryDestination
                .resolve(Path.of(
                        basePokemonInsertFileSegment + getFileSuffix()
                ));

        String basePokemonSpriteInsertFileSegment = "insert_gen%d_significant_pokemon_sprites".formatted(generation);
        pokemonSpriteEntriesInsertionFile = this.directoryDestination
                .resolve(Path.of(
                        basePokemonSpriteInsertFileSegment + getFileSuffix()
                ));
    }

    /**
     * Creates new directory as needed when directory does not exist yet.
     * In addition, to handle cases where specified directory is given, creates a new directory from default one if
     * <li>Directory end-user specified exists but application does not have write privileges</li>
     * <li>Directory end-user specified exists as regular file with same name as specified directory</li>
     *
     * @return The path of newly created directory, null if no new directory created
     */
    public Path createDestinationDirectoryIfNotExists() throws IOException {

        if (Files.notExists(directoryDestination)) {
            return Files.createDirectory(directoryDestination);
        }
        //These are expected to occur when specified directory is given by end user
        else if ((!Files.isWritable(directoryDestination) || Files.isRegularFile(directoryDestination))) {
            directoryDestination = Path.of(DEFAULT_DIR_USED);
            System.out.println("INFO: Default directory was set for use");

            if (!Files.exists(directoryDestination))
                return Files.createDirectory(directoryDestination);
        }

        return null;
    }


    public void writeSpriteRowEntries(List<PokemonSpriteEntry> pokemonSpriteEntries) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(pokemonSpriteEntriesInsertionFile)) {
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

    public Path getPokemonEntriesInsertionFile() {
        return pokemonEntriesInsertionFile;
    }

    public Path getPokemonSpriteEntriesInsertionFile() {
        return pokemonSpriteEntriesInsertionFile;
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
        try (BufferedWriter writer = Files.newBufferedWriter(pokemonEntriesInsertionFile)) {
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