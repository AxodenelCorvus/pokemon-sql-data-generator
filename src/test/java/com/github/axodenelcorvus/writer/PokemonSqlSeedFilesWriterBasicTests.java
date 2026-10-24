package com.github.axodenelcorvus.writer;


import com.github.axodenelcorvus.SupportedGameGenerationRange;
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

class PokemonSqlSeedFilesWriterBasicTests {

    @Test
    @DisplayName("Path object for default directory created correctly when correlating constructor called")
    void testConstructorSetsDefaultDirectory() {
        var testSeedsFilesWriter = new PokemonSqlSeedFilesWriter(1);

        assertEquals(
                PokemonSqlSeedFilesWriter.getDefaultDirDestination(),
                testSeedsFilesWriter.getDirectoryDestination()
        );

        assertTrue(
                testSeedsFilesWriter.getPokemonEntriesInsertionPath()
                            .startsWith(PokemonSqlSeedFilesWriter.getDefaultDirDestination()),
                "pokemonEntriesInsertionFile does not hold correct directory name in relative path"
        );
        assertTrue(
                testSeedsFilesWriter.getPokemonSpriteEntriesInsertionPath()
                            .startsWith(PokemonSqlSeedFilesWriter.getDefaultDirDestination()),
                "pokemonSpriteEntriesInsertionFile does not hold correct directory name in relative path"
        );
    }

    static IntStream validPokemonGenerationRange() {
        return IntStream
                .range(SupportedGameGenerationRange.FIRST_GEN, SupportedGameGenerationRange.LATEST_GEN + 1);
    }

    @ParameterizedTest
    @DisplayName("General format rules being met for PokemonSqlSeedFilesWriter Path objects")
    @MethodSource("validPokemonGenerationRange")
    void testPathObjectsHaveCorrectFormats(int generation) {
        var newTestSeedsFilesWriter =
                new PokemonSqlSeedFilesWriter(generation, Path.of("testDir2000"));

        String pokemonEntriesFileTest = newTestSeedsFilesWriter
                .getPokemonEntriesInsertionPath().getFileName().toString();

        Path pokemonEntriesFileFullPath = newTestSeedsFilesWriter
                .getPokemonEntriesInsertionPath();

        assertTrue(pokemonEntriesFileFullPath.startsWith("testDir2000"),
                "Expected %s directory not found in relative file path %s".formatted("testDir2000", pokemonEntriesFileFullPath.toString()));

        assertTrue(pokemonEntriesFileTest.startsWith("insert_gen%02d_pokemon".formatted(generation)),
                "The insert file does not begin with correct generation or suffix");

        assertTrue(pokemonEntriesFileTest.matches("^.*__\\d+\\.sql$"),
                "%s does not meet expected nanoseconds and file extension suffix of file format of %s".formatted( pokemonEntriesFileTest, ".*__\\d+\\.sql"));
    }

    @Test
    @DisplayName("Construction disallows parent and levels down more than one from current working directory")
    void testDestinationDirectoryRules() {
        assertThrows(InvalidPathException.class,
                () -> new PokemonSqlSeedFilesWriter(1,  Path.of("..")));
        assertThrows(InvalidPathException.class,
                () -> new PokemonSqlSeedFilesWriter(1, Path.of("testDir2000/lvl1")));
        assertThrows(InvalidPathException.class,
                () -> new PokemonSqlSeedFilesWriter(1, Path.of("testDir2000/lvl2/lvl3")));
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    @DisplayName("Check if absolute path disallowed when constructing with one")
    void testAbsolutePathDisallowedInConstruction() {
        assertThrows(InvalidPathException.class,
                () -> new PokemonSqlSeedFilesWriter(1, Path.of("/testDir2000")));
    }


}
