package com.github.axodenelcorvus.model.entry;

import com.github.axodenelcorvus.model.DtoSampleProvider;
import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.axodenelcorvus.GameGenerationScope.GEN_1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PokemonEntriesBundleTests {
    private final DtoSampleProvider dtoSampleProvider = new DtoSampleProvider();

    private PokemonEntriesBundle getScytherEntriesBundle() {
        PokemonSpeciesDTO scytherSpeciesSample = dtoSampleProvider.getPokemonSpeciesDTO(123);
        PokemonDTO scytherSample = dtoSampleProvider.getPokemonDTO(123);

        List<PokemonEntry> scytherEntries = List.of(
                PokemonEntry.from(
                        scytherSample,
                        scytherSpeciesSample,
                        GEN_1.hasFossilPokemon(123)
                )
        );

        List<PokemonSpriteEntry> scytherSpriteEntries = List.of(
                new PokemonSpriteEntry(123, GenderSpriteForm.MALE, RegionalForm.STANDARD, 123),
                new PokemonSpriteEntry(123, GenderSpriteForm.FEMALE, RegionalForm.STANDARD, 123)
        );

        return new PokemonEntriesBundle(scytherEntries, scytherSpriteEntries);
    }

    private PokemonEntriesBundle getFarfetchdEntriesBundle() {
        PokemonSpeciesDTO farfetchdSpeciesSample = dtoSampleProvider.getPokemonSpeciesDTO(10166);
        PokemonDTO farfetchdGalarSample = dtoSampleProvider.getPokemonDTO(10166);
        PokemonDTO farfetchdSample = new PokemonDTO(
                83,
                "farfetchd",
                "normal",
                "flying",
                null
        );

        List<PokemonEntry> farfetchdEntries = List.of(
                PokemonEntry.from(
                        farfetchdSample,
                        farfetchdSpeciesSample,
                        GEN_1.hasFossilPokemon(83)
                ),
                PokemonEntry.from(
                        farfetchdGalarSample,
                        farfetchdSpeciesSample,
                        GEN_1.hasFossilPokemon(83)
                )
        );

        List<PokemonSpriteEntry> farfetchdSpriteEntries = List.of(
                new PokemonSpriteEntry(10166, GenderSpriteForm.NONE, RegionalForm.GALAR, farfetchdSpeciesSample.dexId()),
                new PokemonSpriteEntry(83, GenderSpriteForm.NONE, RegionalForm.STANDARD, farfetchdSpeciesSample.dexId())
        );

        return new PokemonEntriesBundle(farfetchdEntries, farfetchdSpriteEntries);
    }

    /*NOTE: Test Depends on correctness of instantiation for PokemonEntry and PokemonSpriteEntry,
    as well as their equals() methods which can be source of failure. */
    @Test
    @DisplayName("Flatten static method works and returns unmodifiable collection")
    void testFlattenMethod() {
        List<PokemonEntriesBundle> testBundlesList = new ArrayList<>();

        PokemonEntriesBundle actualScytherEntriesBundle = getScytherEntriesBundle();
        PokemonEntriesBundle actualFarfetchdEntriesBundle = getFarfetchdEntriesBundle();

        List<PokemonEntry> allPokemonEntries = new ArrayList<>();
        List<PokemonSpriteEntry> allSpriteEntries = new ArrayList<>();

        allPokemonEntries
                .addAll(actualFarfetchdEntriesBundle.pokemonEntries());
        allPokemonEntries
                .addAll(actualScytherEntriesBundle.pokemonEntries());

        allSpriteEntries
                .addAll(actualFarfetchdEntriesBundle.pokemonSpriteEntries());
        allSpriteEntries
                .addAll(actualScytherEntriesBundle.pokemonSpriteEntries());

        testBundlesList.add(actualFarfetchdEntriesBundle);
        testBundlesList.add(actualScytherEntriesBundle);

        var actualEntriesBundle = PokemonEntriesBundle.flattenBundles(testBundlesList);
        var expectedEntriesBundle = new PokemonEntriesBundle(allPokemonEntries, allSpriteEntries);

        //Test that Pokemon entries meet expected ones
        assertEquals(expectedEntriesBundle.pokemonEntries(), actualEntriesBundle.pokemonEntries());
        assertEquals(expectedEntriesBundle.pokemonSpriteEntries(), actualEntriesBundle.pokemonSpriteEntries());

        //Test that list elements (sprites and pokemon entries) can't be mutated after flattening
        assertThrows(UnsupportedOperationException.class, () -> {
            var entry = actualEntriesBundle.pokemonEntries().getFirst();
            actualEntriesBundle.pokemonEntries().add(entry);
        });

    }

}
