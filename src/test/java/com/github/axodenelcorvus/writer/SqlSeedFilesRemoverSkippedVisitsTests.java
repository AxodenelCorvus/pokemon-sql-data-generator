package com.github.axodenelcorvus.writer;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.abort;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqlSeedFilesRemoverSkippedVisitsTests {
    private final Path DIR_LVLS_USED =
            Path.of("testDir_removal_skip_vists_test_rsrc", "nest_d");

    private final Path VALID_PREFIX_INVALID_EXT =
            DIR_LVLS_USED.getName(0).resolve("insert_gen09_pokemon_26-10-21__203455225.txt");

    private final Path GARBAGE_FILE =
            DIR_LVLS_USED.getName(0).resolve("random.txt");

    private final List<Path> VALID_FILE_INVALID_LVL = List.of(
            DIR_LVLS_USED.resolve("insert_gen09_pokemon_26-10-21__203455225.sql"),
            DIR_LVLS_USED.resolve("insert_gen01_pokemon_26-10-21__203455225.txt")
    );

    private final List<Path> INVALID_PREFIXED_FILES = List.of(
            DIR_LVLS_USED.getName(0).resolve("insert_gen10_pokemon_26-10-21__203455225.txt"),
            DIR_LVLS_USED.getName(0).resolve("insert_gen00_pokemon_26-10-21__203455225.txt"),
            DIR_LVLS_USED.getName(0).resolve("insert_.sql")
    );

    private final List<Path> OUT_OF_TARGET_RANGE_FILES = List.of(
            DIR_LVLS_USED.getName(0).resolve("insert_gen01_pokemon_26-10-21__203455225.txt"),
            DIR_LVLS_USED.getName(0).resolve("insert_gen02_pokemon_26-10-21__203455225.txt"),
            DIR_LVLS_USED.getName(0).resolve("insert_gen03_pokemon_26-10-21__203455225.txt"),
            DIR_LVLS_USED.getName(0).resolve("insert_gen06_pokemon_26-10-21__203455225.txt"),
            DIR_LVLS_USED.getName(0).resolve("insert_gen07_pokemon_26-10-21__203455225.txt"),
            DIR_LVLS_USED.getName(0).resolve("insert_gen08_pokemon_26-10-21__203455225.txt")
    );

    private final int GEN_NINE = 9;

    private final PokemonSqlSeedFilesRemover testSeedFilesRemover =
            new PokemonSqlSeedFilesRemover(GEN_NINE, GEN_NINE, DIR_LVLS_USED.getName(0));

    @BeforeAll
    void usedDirectoryExists() {
        try {
            List<Path> testFileSamplesToCreate = new ArrayList<>(15);

            Files.createDirectories(DIR_LVLS_USED);

            Consumer<Path> addSampleIfExists = (path) -> {
                if (!Files.exists(path))
                    testFileSamplesToCreate.add(path);
            };

            addSampleIfExists.accept(VALID_PREFIX_INVALID_EXT);
            addSampleIfExists.accept(GARBAGE_FILE);

            VALID_FILE_INVALID_LVL.forEach(addSampleIfExists);
            INVALID_PREFIXED_FILES.forEach(addSampleIfExists);
            OUT_OF_TARGET_RANGE_FILES.forEach(addSampleIfExists);

            for (Path testFileSample : testFileSamplesToCreate) {
                Files.createFile(testFileSample);
            }

            testSeedFilesRemover.removeAllTargetedSqlSeedFiles();


        } catch (IOException ioe) {
            abort("Issue setting up directories for tests");
        }
    }


    @ParameterizedTest
    @FieldSource("VALID_FILE_INVALID_LVL")
    @DisplayName("Files where name is valid target, but are nested, not removed")
    void testValidFileNamesInInvalidNestedDir(Path sample) {
        assertTrue(Files.exists(sample));
    }

    @Test
    @DisplayName("File with valid prefix but invalid extension not removed")
    void testFileWithValidPrefixButInvalidExtNotRemoved() {
        assertTrue(Files.exists(VALID_PREFIX_INVALID_EXT));
    }

    @ParameterizedTest
    @FieldSource("OUT_OF_TARGET_RANGE_FILES")
    @DisplayName("Files where prefix contains value that does not match generation range, but are otherwise valid, not removed")
    void testOutOfTargetRangeFilesDefinedInRemover(Path sample) {
        assertTrue(Files.exists(sample));
    }

    @ParameterizedTest
    @FieldSource("INVALID_PREFIXED_FILES")
    @DisplayName("Files with invalid prefix were not removed")
    void testInvalidPrefixedSampleFiles(Path sample) {
        assertTrue(Files.exists(sample));
    }

    @Test
    @DisplayName("Garbage txt file not dumped")
    void testGarbageNotDumped() {
        assertTrue(Files.exists(GARBAGE_FILE));
    }

    @Test
    @DisplayName("Removal count is correct after removal method called (stayed zero)")
    void testRemovalCountCorrect() {
        assertEquals(0, testSeedFilesRemover.getRemovalCount());
    }
    
}
