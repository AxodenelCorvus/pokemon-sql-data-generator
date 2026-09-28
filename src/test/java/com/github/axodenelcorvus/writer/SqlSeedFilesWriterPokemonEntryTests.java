package com.github.axodenelcorvus.writer;

import com.github.axodenelcorvus.entry.PokemonEntry;
import org.junit.jupiter.api.*;


import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.abort;
import static org.junit.jupiter.api.Assertions.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class SqlSeedFilesWriterPokemonEntryTests {
    private final String TEST_DIR = "testDir";
    private final int GENERATION = 1;

    private final PokemonSqlSeedFilesWriter testSeedsFilesWriter =
            new PokemonSqlSeedFilesWriter(GENERATION, TEST_DIR);

    @BeforeEach
    void assumeTestDirectoryIsWritable() {
        try {
            testSeedsFilesWriter.createDestinationDirectoryIfNotExists();

            assumeTrue(Files.isWritable(Path.of(TEST_DIR)));
        } catch (IOException e) {
            abort("Error occurred in creating or checking " + TEST_DIR + " | " + e.getMessage());
        }
    }

    private List<PokemonEntry> supplyPokemonEntriesGenerationOne() {
        return List.of(
                new PokemonEntry(37,37, "Vulpix", 6, "Fire", null, false, false),
                new PokemonEntry(10104, 38, "Ninetales", 6, "Ice", "Fairy", false, false),
                new PokemonEntry(80, 80, "Slowbro", 4, "Water", "Psychic", false, false),
                new PokemonEntry(146, 146, "Moltres", -1, "Fire", null, false, true)
        );
    }

    @Test
    @DisplayName("Test standard entry sample for correctness")
    void testStandardEntryWritesInsertionStatement() throws IOException {
        List<PokemonEntry> testEntries = supplyPokemonEntriesGenerationOne();

        testSeedsFilesWriter.writePokemonRowEntries(testEntries);

        try (BufferedReader reader = Files.newBufferedReader(testSeedsFilesWriter.getPokemonEntriesInsertionFile())) {
            assertAll("Test header of DML statement",
                    () -> assertEquals("INSERT INTO pokemon", reader.readLine()),
                    () -> assertEquals("\t(id, national_dex_id, name, gender_rate, primary_type, secondary_type, is_mythical, is_legendary)", reader.readLine()),
                    () -> assertEquals("VALUES", reader.readLine())
            );

            assertAll("Test row constructors",
                    () -> assertEquals("\t(37, 37, 'Vulpix', 6, 'Fire', null, false, false),", reader.readLine()),
                    () -> assertEquals("\t(10104, 38, 'Ninetales', 6, 'Ice', 'Fairy', false, false),", reader.readLine()),
                    () -> assertEquals("\t(80, 80, 'Slowbro', 4, 'Water', 'Psychic', false, false),", reader.readLine()),
                    () -> assertEquals("\t(146, 146, 'Moltres', null, 'Fire', null, false, true);", reader.readLine(),
                            "Issue in closing line of file of file (line 7)")
            );

            assertNull(reader.readLine(), "More lines written than expected");
        }
    }

    @Test
    @DisplayName("Test writePokemonRowEntries does not mutate passed list")
    void testWritePokemonRowEntriesListParameter() throws IOException {
        List<PokemonEntry> testEntries =
                new ArrayList<>(supplyPokemonEntriesGenerationOne());

        testSeedsFilesWriter.writePokemonRowEntries(testEntries);

        assertEquals(supplyPokemonEntriesGenerationOne(),
                testEntries,
                "Unexpected modification to writePokemonRowEntriesList method's parameter"
        );
    }

    @Test
    @DisplayName("Test writePokemonRowEntries throws expected IOException when called without directory created")
    void testRowEntryWritingThrowsIOException() {
        var newTestSeedsFilesWriter = new PokemonSqlSeedFilesWriter(2, "testDir1236");

        assertThrows(NoSuchFileException.class, () -> {
                newTestSeedsFilesWriter.writePokemonRowEntries(supplyPokemonEntriesGenerationOne());
            }
        );
    }


}
