package com.github.axodenelcorvus.fetcher;

import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;
import com.github.axodenelcorvus.model.dto.PokemonVarietyDTO;
import com.github.axodenelcorvus.model.entry.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Fetches a single Pokemon species DTO on each task's launch, using the state it was initalized with. Then
 * from that it will fetch all the "species" branching regional varieties using the extracted slug names
 * to make additional fetches to Poke API. Upon return, gives a bundled PokemonEntriesBundle with entries created
 * during task.
 */
public final class FetchSingleEntriesPayloadTask implements Callable<PokemonEntriesBundle> {
    private final PokeApiFetcher pokeApiFetcher;
    private final String pokemonSpeciesSlugName;

    public FetchSingleEntriesPayloadTask(PokeApiFetcher pokeApiFetcher, String pokemonSpeciesSlugName) {
        this.pokeApiFetcher = pokeApiFetcher;
        this.pokemonSpeciesSlugName = pokemonSpeciesSlugName;
    }

    public PokemonEntriesBundle call() throws IOException, InterruptedException {
        PokemonSpeciesDTO pokemonSpeciesDTO = pokeApiFetcher.fetchPokemonSpecies(pokemonSpeciesSlugName);

        List<PokemonEntry> pokemonEntries = new ArrayList<>();
        List<PokemonSpriteEntry> pokemonSpriteEntries = new ArrayList<>();

        Map<RegionalForm, String> pokemonSlugNamesByRegion = pullVarietySlugsByRegion(pokemonSpeciesDTO.varieties());


        if (pokemonSpeciesDTO.hasGenderDifferences()) {
            //In nearly all cases this loop will only execute once, but
            //in edges cases like Sneasel it will create 4 entries across two variants
            for (RegionalForm regionForm : pokemonSlugNamesByRegion.keySet()) {
                PokemonDTO pokemonDTO = pokeApiFetcher
                        .fetchPokemon(pokemonSlugNamesByRegion.get(regionForm));
                pokemonEntries
                        .add(PokemonEntry.from(pokemonDTO, pokemonSpeciesDTO));
                pokemonSpriteEntries
                        .add(new PokemonSpriteEntry(pokemonDTO.id(), GenderSpriteForm.MALE, regionForm, pokemonSpeciesDTO.dexId()));
                pokemonSpriteEntries
                        .add(new PokemonSpriteEntry(pokemonDTO.id(), GenderSpriteForm.FEMALE, regionForm, pokemonSpeciesDTO.dexId()));
            }
        }
        else {
            for (RegionalForm regionForm : pokemonSlugNamesByRegion.keySet()) {
                PokemonDTO pokemonDTO = pokeApiFetcher
                        .fetchPokemon(pokemonSlugNamesByRegion.get(regionForm));
                pokemonEntries
                        .add(PokemonEntry.from(pokemonDTO, pokemonSpeciesDTO));
                pokemonSpriteEntries
                        .add(new PokemonSpriteEntry(pokemonDTO.id(), GenderSpriteForm.NONE, regionForm, pokemonSpeciesDTO.dexId()));
            }
        }

        return new PokemonEntriesBundle(pokemonEntries, pokemonSpriteEntries);
    }


    /*
    Loops through each variety DTO and splits slugName by '-' into array of strings. The last index typically
    will contain the region, if any, but not always so search for a regional match starts from end of each array to front. The first segment
    of slug always contains Pokemon's name or first part of their name, so it is omitted from scan.

    Tauros, and future Pokemon, have more than one regional variety tied to a single regional variant group so the map is only
    populated with the first instance of each region. The rest of such edge cases will be handled manually.
     */
    private Map<RegionalForm, String> pullVarietySlugsByRegion(List<PokemonVarietyDTO> variantDTOs) {
        Map<RegionalForm, String> varietiesByRegion = new HashMap<>();

        for (PokemonVarietyDTO varietyDTO : variantDTOs) {
            if (varietyDTO.isDefault()) {
                varietiesByRegion.put(RegionalForm.STANDARD, varietyDTO.slugName());
                continue;
            }

            String[] slugNameSegments = varietyDTO.slugName().split("-");

            for (int i = slugNameSegments.length - 1; i > 0; i--) {
                RegionalForm formFound = RegionalForm.getRegionBy(slugNameSegments[i]);

                if (formFound != null) {
                    varietiesByRegion.putIfAbsent(formFound, varietyDTO.slugName());
                    break;
                }
            }
        }

        return varietiesByRegion;
    }

}