package com.github.axodenelcorvus.writer;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class PokemonSqlSeedFilesWriterTests {

    @Test
    @DisplayName("Path object for default directory created correctly when correlating constructor called")
    void testConstructorSetsDefaultDirectory() {
        var newTestSeedsFilesWriter = new PokemonSqlSeedFilesWriter(1);

        assertNotNull(newTestSeedsFilesWriter.getPokemonEntriesInsertionPath());
        assertNotNull(newTestSeedsFilesWriter.getPokemonSpriteEntriesInsertionPath());

        assertTrue(newTestSeedsFilesWriter.getPokemonEntriesInsertionPath().startsWith(PokemonSqlSeedFilesWriter.DEFAULT_DIRECTORY_DEST),
                "pokemonEntriesInsertionFile does not hold correct directory name in relative path");
        assertTrue(newTestSeedsFilesWriter.getPokemonSpriteEntriesInsertionPath().startsWith(PokemonSqlSeedFilesWriter.DEFAULT_DIRECTORY_DEST),
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
                .getPokemonEntriesInsertionPath().getFileName().toString();

        Path pokemonEntriesFileFullPath = newTestSeedsFilesWriter
                .getPokemonEntriesInsertionPath();

        assertTrue(pokemonEntriesFileFullPath.startsWith("testDir2000"),
                "Expected %s directory not found in relative file path %s".formatted("testDir2000", pokemonEntriesFileFullPath.toString()));

        assertTrue(pokemonEntriesFileTest.startsWith("insert_gen" + generation + "pokemon"),
                "The insert file does not begin with correct generation or suffix");

        assertTrue(pokemonEntriesFileTest.matches("^.*__\\d+\\.sql$"),
                "%s does not meet expected nanoseconds and file extension suffix of file format of %s".formatted( pokemonEntriesFileTest, ".*__\\d+\\.sql"));
    }

    @Test
    @DisplayName("Construction disallows parent and levels down more than one from current working directory")
    void testDestinationDirectoryRules() {
        assertThrows(InvalidPathException.class,
                () -> new PokemonSqlSeedFilesWriter(1, ".."));
        assertThrows(InvalidPathException.class,
                () -> new PokemonSqlSeedFilesWriter(1, "testDir2000/lvl1"));
        assertThrows(InvalidPathException.class,
                () -> new PokemonSqlSeedFilesWriter(1, "testDir2000/lvl2/lvl3"));
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    @DisplayName("Check if absolute path disallowed when constructing with one")
    void testAbsolutePathDisallowedInConstruction() {
        assertThrows(InvalidPathException.class,
                () -> new PokemonSqlSeedFilesWriter(1, "/testDir2000"));
    }


}
