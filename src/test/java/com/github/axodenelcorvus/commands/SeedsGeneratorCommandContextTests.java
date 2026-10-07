package com.github.axodenelcorvus.commands;

import com.github.axodenelcorvus.commands.exceptions.MissingArgumentException;
import com.github.axodenelcorvus.commands.exceptions.RepeatedOptionException;
import com.github.axodenelcorvus.commands.exceptions.UnsupportedOptionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SeedsGeneratorCommandContextTests {
    private final SeedsGeneratorCommandContext generatorCmdContext = new SeedsGeneratorCommandContext();

    @Test
    void testWhenNoInput() {
        assertThrows(MissingArgumentException.class, () -> generatorCmdContext.setup());
    }

    @ParameterizedTest
    @ValueSource(strings = { "-1", "0", "10", "11" })
    void testGivenOutOfRangeGeneration(String outOfRangeGeneration) {
        assertThrows(IllegalArgumentException.class,
                () -> generatorCmdContext.setup(outOfRangeGeneration));
    }

    @ParameterizedTest
    @ValueSource(strings = { "one", "two", "alola", "1 0", "9 ", "generation", "II", "0.1" })
    void testGivenNonParsableGenerationArgument(String incorrectGeneration) {
        assertThrows(IllegalArgumentException.class,
                () -> generatorCmdContext.setup(incorrectGeneration));
    }

    @Test
    void testSetupWithNoOptions() {
        generatorCmdContext.setup("1");
        assertEquals(1, generatorCmdContext.getGenerationScope());
    }


    @ParameterizedTest
    @ValueSource(strings = { "-", "--", "--h", "- -" })
    void testInvalidOptionFormats(String incorrectlyFormattedOption) {
        IllegalArgumentException iae = assertThrows(
                IllegalArgumentException.class,
                () -> generatorCmdContext.setup("1", incorrectlyFormattedOption));
        assertTrue(iae.getMessage().toLowerCase().contains("invalid"));
    }


    @ParameterizedTest
    @ValueSource(strings = { "--version", "--garbage", "--todir", "--directory", "--TO-DIR" })
    void testGivenUnsupportedOption(String unsupportedOption) {
        assertThrows(
                UnsupportedOptionException.class,
                () -> generatorCmdContext.setup("1", unsupportedOption));
    }

    @Test
    void testToDirOptionWithoutRequiredValue() {
        assertThrows(MissingArgumentException.class,
                () -> generatorCmdContext.setup("1", "--to-dir"));
    }

    @Test
    void testToDirOptionRepeatedThrows() {
        assertThrows(RepeatedOptionException.class,
                () -> generatorCmdContext.setup("1", "--to-dir", "testDir2000", "--to-dir", "testingDirectory"));
    }

    static Stream<String> validPokemonGenerationRange() {
        return IntStream
                .range(SeedsGeneratorCommandContext.FIRST_GENERATION, SeedsGeneratorCommandContext.LATEST_GENERATION + 1)
                .mapToObj(String::valueOf);
    }

    @ParameterizedTest
    @MethodSource("validPokemonGenerationRange")
    void testValidSetupWithOptionsForEachGeneration(String generation) {
        generatorCmdContext.setup(generation, "--to-dir", "sql_pokemon");
        assertEquals("sql_pokemon", generatorCmdContext.getOptionalDirectoryDestination());
        assertEquals(Integer.parseInt(generation), generatorCmdContext.getGenerationScope());
    }

    @Test
    @DisplayName("Destination directory != \"to-dir\" when given valid inputs")
    void testDestinationDirectoryNotEqualsToDir() {
        generatorCmdContext.setup("1", "--to-dir", "sql_pokemon");
        assertNotEquals("to-dir", generatorCmdContext.getOptionalDirectoryDestination());
    }

}
