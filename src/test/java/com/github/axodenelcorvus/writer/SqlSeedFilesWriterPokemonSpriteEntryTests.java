package com.github.axodenelcorvus.writer;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.abort;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class SqlSeedFilesWriterPokemonSpriteEntryTests {
    private final String TEST_DIR = "testDir";
    private final int GENERATION = 1;

    private final PokemonSqlSeedFilesWriter testSeedsFilesWriter =
            new PokemonSqlSeedFilesWriter(GENERATION, TEST_DIR);

    @Test
    void testSpriteEntriesFilePathSuffixMatchesExpectedFormat() {

        String pokemonSpriteEntriesFile = testSeedsFilesWriter
                .getPokemonSpriteEntriesInsertionPath().getFileName().toString();

        Path pokemonSpriteEntriesFileFullPath = testSeedsFilesWriter
                .getPokemonSpriteEntriesInsertionPath();

        var expectedSuffix = "insert_gen" + GENERATION + "_significant_pokemon_sprites";

        assertTrue(pokemonSpriteEntriesFileFullPath.startsWith(TEST_DIR),
                "Expected %s directory not found in relative file path %s".formatted("testDir2000", pokemonSpriteEntriesFileFullPath.toString()));

        assertTrue(pokemonSpriteEntriesFile.startsWith(expectedSuffix),
                "%s does not begin with correct suffix expected %s".formatted(pokemonSpriteEntriesFile, expectedSuffix));

    }



}
