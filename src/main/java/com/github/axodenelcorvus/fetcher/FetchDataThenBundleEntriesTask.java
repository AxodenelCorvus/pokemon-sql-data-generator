package com.github.axodenelcorvus.fetcher;

import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;
import com.github.axodenelcorvus.model.dto.PokemonVarietyDTO;
import com.github.axodenelcorvus.model.entry.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Fetches a single Pokemon species DTO on each task's launch, using the state it was initalized with. Then
 * from that it will fetch all the required "species" variants, if any. Then bundles the required Pokemon record entries and Pokemon sprite record entries
 * for return.</br></br>
 * It will also will create the alternative form of the base/default
 * Pokemon entries in special cases where there is only a single stemming alternative form (non-regional) from the base one. These sprites are ones
 * that are deemed significant or regconizable for database used.
 */
public final class FetchDataThenBundleEntriesTask implements Callable<PokemonEntriesBundle> {
    private final PokeApiFetcher pokeApiFetcher;
    private final String pokemonSpeciesSlugName;
    private final Map<Integer, String> singleAltFormDescriptorsById;

    public FetchDataThenBundleEntriesTask(PokeApiFetcher pokeApiFetcher, String pokemonSpeciesSlugName, Map<Integer, String> singleAltFormDescriptorsById) {
        this.pokeApiFetcher = pokeApiFetcher;
        this.pokemonSpeciesSlugName = pokemonSpeciesSlugName;
        this.singleAltFormDescriptorsById = singleAltFormDescriptorsById;
    }

    @Override
    public PokemonEntriesBundle call() throws IOException, InterruptedException {
        PokemonSpeciesDTO pokemonSpeciesDTO = pokeApiFetcher.fetchPokemonSpecies(pokemonSpeciesSlugName);
        PokemonEntriesBundle resultsBundled;

        if (singleAltFormDescriptorsById.containsKey(pokemonSpeciesDTO.dexId()))
            resultsBundled = fetchSingleAltFormEntries(pokemonSpeciesDTO);
        else
            resultsBundled = fetchStandardEntries(pokemonSpeciesDTO);

        for (PokemonEntry pokemonEntry : resultsBundled.pokemonEntries())
            System.out.println('\t' + PokemonEntryFetchDisplayFormatter.formatDisplayFrom(pokemonEntry));

        return resultsBundled;
    }

    private PokemonEntriesBundle fetchStandardEntries(PokemonSpeciesDTO speciesDTO) throws IOException, InterruptedException {
        EnumMap<RegionalForm, PokemonDTO> pokemonByRegion;

        pokemonByRegion = fetchPokemonByRegion(speciesDTO.varieties());

        return PokemonEntriesBundle.fromSpecies(speciesDTO, pokemonByRegion);
    }

    /*
    Loops through each variety DTO and splits slugName by '-' into array of strings. The last index typically
    will contain the region, if any, but not always (for example Tauros) so search for a regional match starts from end of each array to front. The first segment
    of slug always contains Pokemon's name or first part of their name, so it is omitted from scan.

    Tauros, and other possible Pokemon in the future, have more than one regional variety tied to a single regional variant, like a regional variant group
    instead of a single pokemon. A map is used to only populate with the first instance of each region, while
    any special cases with a regional variant grouping like Tauros will be handled through manual entry since Pokemon are rarely
    like this.
     */
    private EnumMap<RegionalForm, PokemonDTO> fetchPokemonByRegion(List<PokemonVarietyDTO> variantDTOs)
            throws IOException, InterruptedException {

        EnumMap<RegionalForm, PokemonDTO> pokemonByRegion = new EnumMap<>(RegionalForm.class);

        for (PokemonVarietyDTO varietyDTO : variantDTOs) {
            if (varietyDTO.isDefault()) {
                PokemonDTO defaultPokemonOfSpecies = pokeApiFetcher
                        .fetchPokemon(varietyDTO.slugName());
                pokemonByRegion.put(RegionalForm.STANDARD, defaultPokemonOfSpecies);
                continue;
            }

            String[] slugNameSegments = varietyDTO.slugName().split("-");

            for (int i = slugNameSegments.length - 1; i > 0; i--) {
                String currNameSegment = slugNameSegments[i];

                //This handles edge case of pikachu-alola-cap
                if (currNameSegment.equals("cap"))
                    break;

                RegionalForm formFound = RegionalForm.getRegionBy(slugNameSegments[i]);
                if (formFound != null) {
                    PokemonDTO regionalFormOfSpecies = pokeApiFetcher
                            .fetchPokemon(varietyDTO.slugName());
                    pokemonByRegion.putIfAbsent(formFound, regionalFormOfSpecies);
                    break;
                }
            }
        }

        return pokemonByRegion;
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
        var altSpriteEntry = new PokemonSpriteEntry(speciesDTO.dexId(), altFormDescriptor);

        pokemonSpriteEntries.add(altSpriteEntry);
        pokemonSpriteEntries.add(defaultSpriteEntry);

        return new PokemonEntriesBundle(pokemonEntries, pokemonSpriteEntries);
    }
}