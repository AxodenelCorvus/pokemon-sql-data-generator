package com.github.axodenelcorvus.writer;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PokemonSqlSeedFilesWriterTests {

    @Test
    @DisplayName("Path object for default directory created correctly when correlating constructor called")
    void testConstructorSetsDefaultDirectory() {
        var newTestSeedsFilesWriter = new PokemonSqlSeedFilesWriter(1);

        assertNotNull(newTestSeedsFilesWriter.getPokemonEntriesInsertionFile());
        assertNotNull(newTestSeedsFilesWriter.getPokemonSpriteEntriesInsertionFile());

        assertTrue(newTestSeedsFilesWriter.getPokemonEntriesInsertionFile().startsWith("generated_sql_files"),
                "pokemonEntriesInsertionFile does not hold correct directory name in relative path");
        assertTrue(newTestSeedsFilesWriter.getPokemonSpriteEntriesInsertionFile().startsWith("generated_sql_files"),
                "pokemonSpriteEntriesInsertionFile does not hold correct directory name in relative path");
    }


    static IntStream validPokemonGenerationRange() {
        return IntStream
                .range(1, 10);
    }


    @ParameterizedTest
    @DisplayName("General format rules being met for PokemonSqlSeedFilesWriter Path objects")
    @MethodSource("validPokemonGenerationRange")
    void testPathObjectsHaveCorrectFormats(int generation) {
        var newTestSeedsFilesWriter = new PokemonSqlSeedFilesWriter(generation, "testDir2000");

        String pokemonEntriesFileTest = newTestSeedsFilesWriter
                .getPokemonEntriesInsertionFile().getFileName().toString();

        Path pokemonEntriesFileFullPath = newTestSeedsFilesWriter
                .getPokemonEntriesInsertionFile();

        assertTrue(pokemonEntriesFileFullPath.startsWith("testDir2000"),
                "Expected %s directory not found in relative file path %s".formatted("testDir2000", pokemonEntriesFileFullPath.toString()));

        assertTrue(pokemonEntriesFileTest.startsWith("insert_gen" + generation + "pokemon"),
                "The insert file does not begin with correct generation or suffix");

        assertTrue(pokemonEntriesFileTest.matches("^.*__\\d+\\.sql$"),
                "%s does not meet expected nanoseconds and file extension suffix of file format of %s".formatted( pokemonEntriesFileTest, ".*__\\d+\\.sql"));
    }

}
