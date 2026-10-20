package com.github.axodenelcorvus.writer;

import com.github.axodenelcorvus.model.DtoSampleProvider;
import com.github.axodenelcorvus.model.entry.GenderSpriteForm;
import com.github.axodenelcorvus.model.entry.PokemonEntry;
import com.github.axodenelcorvus.model.entry.PokemonSpriteEntry;
import com.github.axodenelcorvus.model.entry.RegionalForm;
import org.junit.jupiter.api.*;


import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.abort;
import static org.junit.jupiter.api.Assertions.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SqlSeedFilesWriterAndRemoverPokemonEntryTests {
    private final Path TEST_DIR = Path.of("testDir");
    private final int GENERATION_ONE = 1;
    private final int GENERATION_TWO = 2;

    private final DtoSampleProvider dtoProvider = new DtoSampleProvider();

    private final PokemonSqlSeedFilesWriter testSeedsFilesWriterOne =
                new PokemonSqlSeedFilesWriter(GENERATION_ONE, TEST_DIR);
    private final PokemonSqlSeedFilesWriter testSeedsFilesWriterTwo =
                new PokemonSqlSeedFilesWriter(GENERATION_TWO, TEST_DIR);

    private final PokemonSqlSeedFilesRemover testSeedsFileRemover =
                new PokemonSqlSeedFilesRemover(GENERATION_ONE, GENERATION_TWO, TEST_DIR);

    @BeforeEach
    void assumeTestDirectoryIsWritable() {
        try {
            testSeedsFilesWriterOne.createDestinationDirectoryIfNotExists();
            assumeTrue(Files.isWritable(TEST_DIR));
            assumeTrue(TEST_DIR.equals(testSeedsFileRemover.getTargetedDirectory()));
        } catch (IOException e) {
            abort("Error occurred in creating or checking " + TEST_DIR + " | " + e.getMessage());
        }
    }

    //Supplies all Pokemon introduced in generation one, but also including variant forms (produced later)
    private List<PokemonEntry> supplyPokemonEntriesGenerationOne() {
        return List.of(
                //Vulpix
                PokemonEntry.from(dtoProvider.getPokemonDTO(37), dtoProvider.getPokemonSpeciesDTO(37)),
                //Alolan Ninetales
                PokemonEntry.from(dtoProvider.getPokemonDTO(10104), dtoProvider.getPokemonSpeciesDTO(10104)),
                //Slowbro
                PokemonEntry.from(dtoProvider.getPokemonDTO(80), dtoProvider.getPokemonSpeciesDTO(80)),
                //Moltres
                PokemonEntry.from(dtoProvider.getPokemonDTO(146), dtoProvider.getPokemonSpeciesDTO(146))
        );
    }

    private List<PokemonSpriteEntry> supplyPokemonSpriteEntriesGenerationOne() {
        return List.of(
                new PokemonSpriteEntry(37, GenderSpriteForm.NONE, RegionalForm.STANDARD, 37),
                new PokemonSpriteEntry(10104, GenderSpriteForm.NONE, RegionalForm.ALOLA, 38),
                new PokemonSpriteEntry(80, GenderSpriteForm.NONE, RegionalForm.STANDARD, 80),
                new PokemonSpriteEntry(146, GenderSpriteForm.NONE, RegionalForm.STANDARD, 146)
        );
    }

    private List<PokemonEntry> supplyPokemonEntriesGenerationTwo() {
        return List.of(
                //Chikorita
                PokemonEntry.from(dtoProvider.getPokemonDTO(152), dtoProvider.getPokemonSpeciesDTO(152)),
                //Hisusian Sneasel
                PokemonEntry.from(dtoProvider.getPokemonDTO(10235), dtoProvider.getPokemonSpeciesDTO(10235)),
                //Heracross
                PokemonEntry.from(dtoProvider.getPokemonDTO(214), dtoProvider.getPokemonSpeciesDTO(214)),
                //Raikou
                PokemonEntry.from(dtoProvider.getPokemonDTO(243), dtoProvider.getPokemonSpeciesDTO(243))
        );
    }

    private List<PokemonSpriteEntry> supplyPokemonSpriteEntriesGenerationTwo() {
        return List.of(
                new PokemonSpriteEntry(152, GenderSpriteForm.NONE, RegionalForm.STANDARD, 152),
                new PokemonSpriteEntry(10235, GenderSpriteForm.MALE, RegionalForm.HISUI, 215),
                new PokemonSpriteEntry(10235, GenderSpriteForm.FEMALE, RegionalForm.HISUI, 215),
                new PokemonSpriteEntry(214, GenderSpriteForm.MALE, RegionalForm.STANDARD, 214),
                new PokemonSpriteEntry(214, GenderSpriteForm.FEMALE, RegionalForm.STANDARD, 214),
                new PokemonSpriteEntry(243, GenderSpriteForm.NONE, RegionalForm.STANDARD, 243)
        );
    }

    @Test
    @Order(1)
    @DisplayName("Test writePokemonRowEntries throws expected IOException when called without directory created")
    void testRowEntryWritingThrowsIOException() {
        var newTestSeedsFilesWriter = new PokemonSqlSeedFilesWriter(2, Path.of("testDir1236"));

        assertThrows(NoSuchFileException.class, () -> {
                    newTestSeedsFilesWriter.writePokemonRowEntries(supplyPokemonEntriesGenerationOne());
                }
        );
    }

    @Test
    @Order(1)
    @DisplayName("Generation one SQL file for pokemon table is created, readable, and contents are correct")
    void testGenOneEntryInsertionStatementWrite() throws IOException {
        List<PokemonEntry> testEntries = supplyPokemonEntriesGenerationOne();

        testSeedsFilesWriterOne.writePokemonRowEntries(testEntries);

        try (BufferedReader reader = Files.newBufferedReader(testSeedsFilesWriterOne.getPokemonEntriesInsertionPath())) {
            assertAll("Header of DML statement",
                    () -> assertEquals("INSERT INTO pokemon", reader.readLine()),
                    () -> assertEquals("\t(id, national_dex_id, name, gender_rate, primary_type, secondary_type, is_mythical, is_legendary)", reader.readLine()),
                    () -> assertEquals("VALUES", reader.readLine())
            );

            var closeLineFailMsg = "Issue in closing line of file (line 7)";

            assertAll("Row constructors",
                    () -> assertEquals("\t(37, 37, 'Vulpix', 6, 'Fire', null, false, false),", reader.readLine()),
                    () -> assertEquals("\t(10104, 38, 'Ninetales', 6, 'Ice', 'Fairy', false, false),", reader.readLine()),
                    () -> assertEquals("\t(80, 80, 'Slowbro', 4, 'Water', 'Psychic', false, false),", reader.readLine()),
                    () -> assertEquals("\t(146, 146, 'Moltres', null, 'Fire', null, false, true);", reader.readLine(), closeLineFailMsg)
            );

            assertNull(reader.readLine(), "More lines written than expected");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Generation one SQL file for significant_sprite_forms table is created, readable, and contents are correct")
    void testGenOneEntrySpritesInsertionStatementWrite() throws IOException {
        List<PokemonSpriteEntry> testEntries = supplyPokemonSpriteEntriesGenerationOne();

        testSeedsFilesWriterOne.writeSpriteRowEntries(testEntries);

        try (BufferedReader reader = Files.newBufferedReader(testSeedsFilesWriterOne.getPokemonSpriteEntriesInsertionPath())) {
            assertAll("Header of DML statement",
                    () -> assertEquals("INSERT INTO significant_sprite_forms", reader.readLine()),
                    () -> assertEquals("\t(pokemon_id, descriptor, static_img_resource_path)", reader.readLine()),
                    () -> assertEquals("VALUES", reader.readLine())
            );

            var closeLineFailMsg = "Issue in closing line of file (line 7)";

            assertAll("Row constructors",
                    () -> assertEquals("\t(37, 'Default', '/universal_default/37.png'),", reader.readLine()),
                    () -> assertEquals("\t(10104, 'Default Alola', '/universal_default/38_alola.png'),", reader.readLine()),
                    () -> assertEquals("\t(80, 'Default', '/universal_default/80.png'),", reader.readLine()),
                    () -> assertEquals("\t(146, 'Default', '/universal_default/146.png');", reader.readLine(), closeLineFailMsg)
            );

            assertNull(reader.readLine(), "More lines written than expected");
        }
    }

    @Test
    @Order(3)
    @DisplayName("Generation two SQL file for pokemon table is created, readable, and contents are correct")
    void testGenTwoEntryInsertionStatementWrite() throws IOException {
        List<PokemonEntry> testEntries = supplyPokemonEntriesGenerationTwo();

        testSeedsFilesWriterTwo.writePokemonRowEntries(testEntries);

        try (BufferedReader reader = Files.newBufferedReader(testSeedsFilesWriterTwo.getPokemonEntriesInsertionPath())) {
            assertAll("Header of DML statement",
                    () -> assertEquals("INSERT INTO pokemon", reader.readLine()),
                    () -> assertEquals("\t(id, national_dex_id, name, gender_rate, primary_type, secondary_type, is_mythical, is_legendary)", reader.readLine()),
                    () -> assertEquals("VALUES", reader.readLine())
            );

            var closeLineFailMsg = "Issue in closing line of file (line 7)";

            assertAll("Row constructors",
                    () -> assertEquals("\t(152, 152, 'Chikorita', 1, 'Grass', null, false, false),", reader.readLine()),
                    () -> assertEquals("\t(10235, 215, 'Sneasel', 4, 'Fighting', 'Poison', false, false),", reader.readLine()),
                    () -> assertEquals("\t(214, 214, 'Heracross', 4, 'Bug', 'Fighting', false, false),", reader.readLine()),
                    () -> assertEquals("\t(243, 243, 'Raikou', null, 'Electric', null, false, true);", reader.readLine(), closeLineFailMsg)
            );

            assertNull(reader.readLine(), "More lines written than expected");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Generation two SQL file for significant_sprite_forms table is created, readable, and contents are correct")
    void testGenTwoEntrySpritesInsertionStatementWrite() throws IOException {
        List<PokemonSpriteEntry> testEntries = supplyPokemonSpriteEntriesGenerationTwo();

        testSeedsFilesWriterTwo.writeSpriteRowEntries(testEntries);

        try (BufferedReader reader = Files.newBufferedReader(testSeedsFilesWriterTwo.getPokemonSpriteEntriesInsertionPath())) {
            assertAll("Test header of DML statement",
                    () -> assertEquals("INSERT INTO significant_sprite_forms", reader.readLine()),
                    () -> assertEquals("\t(pokemon_id, descriptor, static_img_resource_path)", reader.readLine()),
                    () -> assertEquals("VALUES", reader.readLine())
            );

            var closeLineFailMsg = "Issue in closing line of file (line 9)";

            assertAll("Test row constructors",
                    () -> assertEquals("\t(152, 'Default', '/universal_default/152.png'),", reader.readLine()),
                    () -> assertEquals("\t(10235, 'Male Hisui', '/universal_default/215_hisui.png'),", reader.readLine()),
                    () -> assertEquals("\t(10235, 'Female Hisui', '/female/215_hisui.png'),", reader.readLine()),
                    () -> assertEquals("\t(214, 'Male', '/universal_default/214.png'),", reader.readLine()),
                    () -> assertEquals("\t(214, 'Female', '/female/214.png'),", reader.readLine()),
                    () -> assertEquals("\t(243, 'Default', '/universal_default/243.png');", reader.readLine(), closeLineFailMsg)
            );

            assertNull(reader.readLine(), "More lines written than expected");
        }
    }

}
