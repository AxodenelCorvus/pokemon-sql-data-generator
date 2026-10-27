package com.github.axodenelcorvus.fetcher;

import com.github.axodenelcorvus.model.DtoSampleProvider;
import com.github.axodenelcorvus.model.entry.PokemonEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.axodenelcorvus.fetcher.PokemonEntryFetchDisplay.END_BOUND_OF_MID_COLUMN;
import static com.github.axodenelcorvus.fetcher.PokemonEntryFetchDisplay.TOTAL_LENGTH_OF_ENTRY_DISPLAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PokemonEntryFetchDisplayTest {
    private static final DtoSampleProvider dtoSampleProvider = new DtoSampleProvider();
    private final int vulpixPokemonId = 37;
    private final int hisuiFarfetchdId = 10166;

    @Test
    @DisplayName("Case with null 2nd type field formatted correctly")
    void testFormatUsingNullSecondaryTypeCase() {
        boolean isSpeciesFossil = false;

        PokemonEntry testEntry = PokemonEntry.from(
                dtoSampleProvider.getPokemonDTO(vulpixPokemonId),
                dtoSampleProvider.getPokemonSpeciesDTO(vulpixPokemonId),
                isSpeciesFossil
        );

        String expectedEntryFetchDisplay =
            "        Primary Key: 37    | Dex ID: 0037 | Type1: Fire     | Type2: N/A     ";

        String actualEntryFetchDisplay =
                PokemonEntryFetchDisplay.formatDisplayFrom(testEntry);

        assertEquals(expectedEntryFetchDisplay, actualEntryFetchDisplay);
    }

    @Test
    @DisplayName("Case with max type length and max primary key length used")
    void testWithWithSomePackedLengthFields() {
        boolean isSpeciesFossil = false;

        PokemonEntry testEntry = PokemonEntry.from(
                dtoSampleProvider.getPokemonDTO(hisuiFarfetchdId),
                dtoSampleProvider.getPokemonSpeciesDTO(hisuiFarfetchdId),
                isSpeciesFossil
        );

        String expectedEntryFetchDisplay =
                "        Primary Key: 10166 | Dex ID: 0083 | Type1: Fighting | Type2: N/A     ";

        int expectedEntryFetchDisplayLength = 77;


        String actualEntryFetchDisplay =
                PokemonEntryFetchDisplay.formatDisplayFrom(testEntry);

        assertEquals(expectedEntryFetchDisplay, actualEntryFetchDisplay);
        assertEquals(expectedEntryFetchDisplayLength, actualEntryFetchDisplay.length());
    }

    @Test
    @DisplayName("Class constants correct")
    void testConstantsCorrect() {
        assertEquals(77, TOTAL_LENGTH_OF_ENTRY_DISPLAYS);
        assertEquals(43, END_BOUND_OF_MID_COLUMN);
    }

    @Test
    @DisplayName("Full message with two Samples")
    void testFullMessageWithFewSamples() {
        boolean isSpeciesFossil = false;

        List<PokemonEntry> testEntries = List.of(
                PokemonEntry.from(
                        dtoSampleProvider.getPokemonDTO(vulpixPokemonId),
                        dtoSampleProvider.getPokemonSpeciesDTO(vulpixPokemonId),
                        isSpeciesFossil
                ),
                PokemonEntry.from(
                        dtoSampleProvider.getPokemonDTO(hisuiFarfetchdId),
                        dtoSampleProvider.getPokemonSpeciesDTO(hisuiFarfetchdId),
                        isSpeciesFossil
                )
        );

        String fetchEntryHeader = PokemonEntryFetchDisplay.getHeaderDisplay() + '\n';
        String vulpixEntryDisplay =
                PokemonEntryFetchDisplay.formatDisplayFrom(testEntries.get(0)) + '\n';
        String hisuiFarfetchdEntryDisplay =
                PokemonEntryFetchDisplay.formatDisplayFrom(testEntries.get(1));

        String actualFullDisplay = fetchEntryHeader + vulpixEntryDisplay + hisuiFarfetchdEntryDisplay;

        String expectedFullDisplay =
                "                                (--ENTRIES FETCHED--)" + '\n' +
                "        Primary Key: 37    | Dex ID: 0037 | Type1: Fire     | Type2: N/A     " + '\n' +
                "        Primary Key: 10166 | Dex ID: 0083 | Type1: Fighting | Type2: N/A     ";


        assertEquals(expectedFullDisplay, actualFullDisplay);
    }

}
