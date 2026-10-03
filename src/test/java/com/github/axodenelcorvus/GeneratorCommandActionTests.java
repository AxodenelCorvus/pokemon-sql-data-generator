package com.github.axodenelcorvus;

import com.github.axodenelcorvus.commands.GeneratorCommandAction;
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

class GeneratorCommandActionTests {
    private final GeneratorCommandAction generatorCommand = new GeneratorCommandAction();


    @Test
    void testWhenNoInput() {
        MissingArgumentException thrown = assertThrows(MissingArgumentException.class, () -> generatorCommand.setup());
        assertFalse(generatorCommand.isSetup(),
                "Generator command isSetup is true, when should be false");
    }

    @ParameterizedTest
    @ValueSource(strings = { "-1", "0", "10", "11" })
    void testGivenOutOfRangeGeneration(String outOfRangeGeneration) {
        assertThrows(IllegalArgumentException.class,
                () -> generatorCommand.setup(outOfRangeGeneration));
        assertFalse(generatorCommand.isSetup(),
                "Generator command isSetup is true, when should be false");
    }

    @ParameterizedTest
    @ValueSource(strings = { "one", "two", "alola", "1 0", "9 ", "generation", "II", "0.1" })
    void testGivenNonParsableGenerationArgument(String incorrectGeneration) {
        assertThrows(IllegalArgumentException.class,
                () -> generatorCommand.setup(incorrectGeneration));
        assertFalse(generatorCommand.isSetup(),
                "Generator command isSetup is true, when should be false");
    }

    @Test
    void testSetupWithNoOptions() {
        generatorCommand.setup("1");
        assertTrue(generatorCommand.isSetup(),
                "Generator command was not updated to valid runnable state");
        assertEquals(1, generatorCommand.getGenerationScope());
    }


    @ParameterizedTest
    @ValueSource(strings = { "-", "--", "--h", "- -" })
    void testInvalidOptionFormats(String incorrectlyFormattedOption) {
        IllegalArgumentException iae = assertThrows(
                IllegalArgumentException.class,
                () -> generatorCommand.setup("1", incorrectlyFormattedOption));
        assertTrue(iae.getMessage().toLowerCase().contains("invalid"));
        assertFalse(generatorCommand.isSetup(),
                "Generator command isSetup is true, when should be false");
    }


    @ParameterizedTest
    @ValueSource(strings = { "--version", "--garbage", "--todir", "--directory", "--TO-DIR" })
    void testGivenUnsupportedOption(String unsupportedOption) {
        assertThrows(
                UnsupportedOptionException.class,
                () -> generatorCommand.setup("1", unsupportedOption));
        assertFalse(generatorCommand.isSetup(),
                "Generator command isSetup is true, when should be false");
    }

    @Test
    void testToDirOptionWithoutRequiredValue() {
        assertThrows(MissingArgumentException.class,
                () -> generatorCommand.setup("1", "--to-dir"));
        assertFalse(generatorCommand.isSetup(),
                "Generator command isSetup is true, when should be false");
    }

    @Test
    void testToDirOptionRepeatedThrows() {
        assertThrows(RepeatedOptionException.class,
                () -> generatorCommand.setup("1", "--to-dir", "testDir2000", "--to-dir", "testingDirectory"));
        assertFalse(generatorCommand.isSetup(),
                "Generator command isSetup is true, when should be false");
    }

    static Stream<String> validPokemonGenerationRange() {
        return IntStream
                .range(GeneratorCommandAction.FIRST_GENERATION, GeneratorCommandAction.LATEST_GENERATION + 1)
                .mapToObj(String::valueOf);
    }

    @ParameterizedTest
    @MethodSource("validPokemonGenerationRange")
    void testValidSetupWithOptionsForEachGeneration(String generation) {
        assertAll("Start state test",
                () -> assertFalse(generatorCommand.isSetup()),
                () -> assertFalse(generatorCommand.hasBeenExhausted())
        );

        generatorCommand.setup(generation, "--to-dir", "sql_pokemon");
        assertTrue(generatorCommand.isSetup());
        assertEquals("sql_pokemon", generatorCommand.getOptionalDirectoryDestination());
        assertEquals(Integer.parseInt(generation), generatorCommand.getGenerationScope());
        assertFalse(generatorCommand.hasBeenExhausted(),
                "setup changes hasBeenExhausted field inside when it should not");
    }

    @Test
    @DisplayName("Destination directory != \"to-dir\" when given valid inputs")
    void testDestinationDirectoryNotEqualsToDir() {
        generatorCommand.setup("1", "--to-dir", "sql_pokemon");
        assertTrue(generatorCommand.isSetup());
        assertNotEquals("to-dir", generatorCommand.getOptionalDirectoryDestination());
    }

}
