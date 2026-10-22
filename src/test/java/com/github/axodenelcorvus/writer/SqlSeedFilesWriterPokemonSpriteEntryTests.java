package com.github.axodenelcorvus.writer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlSeedFilesWriterPokemonSpriteEntryTests {
    private final Path TEST_DIR = Path.of("testDir");
    private final int GENERATION = 1;

    private final PokemonSqlSeedFilesWriter testSeedsFilesWriter =
            new PokemonSqlSeedFilesWriter(GENERATION, TEST_DIR);

    @Test
    @DisplayName("Sprites entries file path suffix matches expected format")
    void testSpriteEntriesFilePathSuffixMatchesExpectedFormat() {

        String pokemonSpriteEntriesFile = testSeedsFilesWriter
                .getPokemonSpriteEntriesInsertionPath().getFileName().toString();

        Path pokemonSpriteEntriesFileFullPath = testSeedsFilesWriter
                .getPokemonSpriteEntriesInsertionPath();

        var expectedSuffix = "insert_gen%02d_significant_pokemon_sprites".formatted(GENERATION);

        assertTrue(pokemonSpriteEntriesFileFullPath.startsWith(TEST_DIR),
                "Expected %s directory not found in relative file path %s".formatted("testDir2000", pokemonSpriteEntriesFileFullPath.toString()));

        assertTrue(pokemonSpriteEntriesFile.startsWith(expectedSuffix),
                "%s does not begin with correct suffix expected %s".formatted(pokemonSpriteEntriesFile, expectedSuffix));

    }

}
