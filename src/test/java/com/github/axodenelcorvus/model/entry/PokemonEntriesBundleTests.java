package com.github.axodenelcorvus.model.entry;

import com.github.axodenelcorvus.model.DtoSampleProvider;
import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PokemonEntriesBundleTests {
    private final DtoSampleProvider dtoSampleProvider = new DtoSampleProvider();

    @Test
    @DisplayName("Test flatten method result's correctness and returns immutable collection/list")
    void testFlattenMethod() {
        List<PokemonEntriesBundle> testBundlesList = new ArrayList<>();

        PokemonSpeciesDTO farfetchdSpeciesSample = dtoSampleProvider.getPokemonSpeciesDTO(10166);
        PokemonDTO farfetchdGalarSample = dtoSampleProvider.getPokemonDTO(10166);
        var farfetchdSample = new PokemonDTO(farfetchdSpeciesSample.dexId(), "farfetchd", "normal", "flying");


        PokemonSpeciesDTO scytherSpeciesSample = dtoSampleProvider.getPokemonSpeciesDTO(123);
        PokemonDTO scytherSample = dtoSampleProvider.getPokemonDTO(123);

        List<PokemonEntry> farfetchdEntries = List.of(
                PokemonEntry.from(farfetchdSample, farfetchdSpeciesSample),
                PokemonEntry.from(farfetchdGalarSample, farfetchdSpeciesSample)
        );
        List<PokemonSpriteEntry> farfetchdSpriteEntries = List.of(
                new PokemonSpriteEntry(83, GenderSpriteForm.NONE, RegionalForm.GALAR, farfetchdSpeciesSample.dexId()),
                new PokemonSpriteEntry(83, GenderSpriteForm.NONE, RegionalForm.STANDARD, farfetchdSpeciesSample.dexId())
        );


        List<PokemonEntry> scytherEntries = List.of(PokemonEntry.from(scytherSample, scytherSpeciesSample));
        List<PokemonSpriteEntry> scytherSpriteEntries = List.of(
                new PokemonSpriteEntry(123, GenderSpriteForm.MALE, RegionalForm.STANDARD, 123),
                new PokemonSpriteEntry(123, GenderSpriteForm.FEMALE, RegionalForm.STANDARD, 123)
        );

        List<PokemonEntry> allEntries = new ArrayList<>();
        allEntries.addAll(farfetchdEntries);
        allEntries.addAll(scytherEntries);

        List<PokemonSpriteEntry> allSpriteEntries = new ArrayList<>();
        allSpriteEntries.addAll(farfetchdSpriteEntries);
        allSpriteEntries.addAll(scytherSpriteEntries);

        testBundlesList.add(new PokemonEntriesBundle(farfetchdEntries, farfetchdSpriteEntries));
        testBundlesList.add(new PokemonEntriesBundle(scytherEntries, scytherSpriteEntries));

        var actualEntriesBundle = PokemonEntriesBundle.flattenBundles(testBundlesList);
        var expectedEntriesBundle = new PokemonEntriesBundle(allEntries, allSpriteEntries);

        assertEquals(expectedEntriesBundle.pokemonEntries(), actualEntriesBundle.pokemonEntries());
        assertEquals(expectedEntriesBundle.pokemonSpriteEntries(), actualEntriesBundle.pokemonSpriteEntries());

        assertThrows(UnsupportedOperationException.class, () -> {
            actualEntriesBundle.pokemonEntries()
                    .add(PokemonEntry.from(scytherSample, scytherSpeciesSample));
        });
        
        assertThrows(UnsupportedOperationException.class, () -> {
            actualEntriesBundle.pokemonSpriteEntries()
                    .add(new PokemonSpriteEntry(123, GenderSpriteForm.MALE, RegionalForm.STANDARD, 123));
        });
    }

}
