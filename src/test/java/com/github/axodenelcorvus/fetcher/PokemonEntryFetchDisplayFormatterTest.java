package com.github.axodenelcorvus.fetcher;

import com.github.axodenelcorvus.SupportedGameGenerationRange;
import com.github.axodenelcorvus.model.DtoSampleProvider;
import com.github.axodenelcorvus.model.entry.FossilPokemonChecker;
import com.github.axodenelcorvus.model.entry.PokemonEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PokemonEntryFetchDisplayFormatterTest {
    private final DtoSampleProvider dtoSampleProvider = new DtoSampleProvider();
    private final FossilPokemonChecker unusedFossilPokemonChecker =
            new FossilPokemonChecker(SupportedGameGenerationRange.LATEST_GEN + 1);

    @Test
    @DisplayName("Case with null 2nd type field formatted correctly")
    void testFormatUsingNullSecondaryTypeCase() {
        int vulpixPokemonId = 37;


        PokemonEntry testEntry = PokemonEntry.from(
                dtoSampleProvider.getPokemonDTO(vulpixPokemonId),
                dtoSampleProvider.getPokemonSpeciesDTO(vulpixPokemonId),
                unusedFossilPokemonChecker
        );

        String expectedEntryFetchDisplay =
            "Pokemon Entry Collected)  Primary Key: 37    | Dex ID: 0037 | Type1: Fire     | Type2: N/A";

        String actualEntryFetchDisplay =
                PokemonEntryFetchDisplayFormatter.formatDisplayFrom(testEntry);

        assertEquals(expectedEntryFetchDisplay, actualEntryFetchDisplay);
    }

    @Test
    @DisplayName("Case with max type length and max primary key length used")
    void testWithWithSomePackedLengthFields() {
        int hisuiFarfetchdId = 10166;

        PokemonEntry testEntry = PokemonEntry.from(
                dtoSampleProvider.getPokemonDTO(hisuiFarfetchdId),
                dtoSampleProvider.getPokemonSpeciesDTO(hisuiFarfetchdId),
                unusedFossilPokemonChecker
        );

        String expectedEntryFetchDisplay =
                "Pokemon Entry Collected)  Primary Key: 10166 | Dex ID: 0083 | Type1: Fighting | Type2: N/A";

        String actualEntryFetchDisplay =
                PokemonEntryFetchDisplayFormatter.formatDisplayFrom(testEntry);

        assertEquals(expectedEntryFetchDisplay, actualEntryFetchDisplay);
    }
}
