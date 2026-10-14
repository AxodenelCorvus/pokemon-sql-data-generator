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
 * from that it will fetch all the required "species" variants, if any. Then compiles/bundles the required Pokemon entries and Pokemon sprite entries
 * for return.</br></br>
 * It will also will create the alternative form of the base/default
 * Pokemon entries in special cases where there is only a single stemming alternative form (non-regional) from the base one. These sprites are ones
 * that are deemed significant or regconizable for database used.
 */
public final class FetchThenCompileBundleEntriesTask implements Callable<PokemonEntriesBundle> {
    private final PokeApiFetcher pokeApiFetcher;
    private final String pokemonSpeciesSlugName;
    private final Map<Integer, String> singleAltFormDescriptorsById;

    public FetchThenCompileBundleEntriesTask(PokeApiFetcher pokeApiFetcher, String pokemonSpeciesSlugName, Map<Integer, String> singleAltFormDescriptorsById) {
        this.pokeApiFetcher = pokeApiFetcher;
        this.pokemonSpeciesSlugName = pokemonSpeciesSlugName;
        this.singleAltFormDescriptorsById = singleAltFormDescriptorsById;
    }

    @Override
    public PokemonEntriesBundle call() throws IOException, InterruptedException {
        PokemonSpeciesDTO pokemonSpeciesDTO = pokeApiFetcher.fetchPokemonSpecies(pokemonSpeciesSlugName);

        if (singleAltFormDescriptorsById.containsKey(pokemonSpeciesDTO.dexId()))
            return fetchSingleAltFormEntries(pokemonSpeciesDTO);
        else
            return fetchStandardEntries(pokemonSpeciesDTO);
    }

    private PokemonEntriesBundle fetchStandardEntries(PokemonSpeciesDTO speciesDTO) throws IOException, InterruptedException {
        List<PokemonEntry> pokemonEntries = new ArrayList<>();
        List<PokemonSpriteEntry> pokemonSpriteEntries = new ArrayList<>();

        Map<RegionalForm, String> pokemonSlugNamesByRegion = pullVarietySlugsByRegion(speciesDTO.varieties());

        for (RegionalForm regionForm : pokemonSlugNamesByRegion.keySet()) {
            PokemonDTO pokemonDTO = pokeApiFetcher
                    .fetchPokemon(pokemonSlugNamesByRegion.get(regionForm));
            pokemonEntries
                    .add(PokemonEntry.from(pokemonDTO, speciesDTO));

            if (speciesDTO.hasGenderDifferences()) {
                pokemonSpriteEntries
                        .add(new PokemonSpriteEntry(pokemonDTO.id(), GenderSpriteForm.MALE, regionForm, speciesDTO.dexId()));
                pokemonSpriteEntries
                        .add(new PokemonSpriteEntry(pokemonDTO.id(), GenderSpriteForm.FEMALE, regionForm, speciesDTO.dexId()));
            } else {
                pokemonSpriteEntries
                        .add(new PokemonSpriteEntry(pokemonDTO.id(), GenderSpriteForm.NONE, regionForm, speciesDTO.dexId()));
            }
        }

        return new PokemonEntriesBundle(pokemonEntries, pokemonSpriteEntries);
    }

    /*
    Loops through each variety DTO and splits slugName by '-' into array of strings. The last index typically
    will contain the region, if any, but not always (for example Tauros) so search for a regional match starts from end of each array to front. The first segment
    of slug always contains Pokemon's name or first part of their name, so it is omitted from scan.

    Tauros, and other possible Pokemon in the future, have more than one regional variety tied to a single regional variant but the map is only
    populated with the first instance of each region. The rest of such an edge case will be handled through manual entry since few Pokemon are like this.
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

    private PokemonEntriesBundle fetchSingleAltFormEntries(PokemonSpeciesDTO speciesDTO) throws IOException, InterruptedException {
        List<PokemonEntry> pokemonEntries = new ArrayList<>();
        List<PokemonSpriteEntry> pokemonSpriteEntries = new ArrayList<>();

        for (PokemonVarietyDTO variety : speciesDTO.varieties()) {
            if (variety.isDefault()) {
                PokemonDTO defaultFormDTO = pokeApiFetcher.fetchPokemon(variety.slugName()) ;
                pokemonEntries.add(PokemonEntry.from(defaultFormDTO, speciesDTO));
                break;
            }
        }

        int pokemonId = pokemonEntries.getFirst().getId();

        String altFormDescriptor = singleAltFormDescriptorsById.get(pokemonId);

        var defaultSpriteEntry = new PokemonSpriteEntry(pokemonId, GenderSpriteForm.NONE, RegionalForm.STANDARD, speciesDTO.dexId());
        var altSpriteEntry = new PokemonSpriteEntry(pokemonId, altFormDescriptor, speciesDTO.dexId());

        pokemonSpriteEntries.add(altSpriteEntry);
        pokemonSpriteEntries.add(defaultSpriteEntry);

        return new PokemonEntriesBundle(pokemonEntries, pokemonSpriteEntries);
    }

}