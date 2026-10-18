package com.github.axodenelcorvus.model.entry;

import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

public record PokemonEntriesBundle(
        List<PokemonEntry> pokemonEntries,
        List<PokemonSpriteEntry> pokemonSpriteEntries)
{

    /**
     * Bundles together the Pokemon entry records and significant Pokemon entry sprite records from
     * related DTOs to form a singe bundle for a Pokemon "species" group. Care must be considered in use as
     * there is no checks for correctness on DTOs in parameter.
     * @param baseSpecies The PokemonSpeciesDTO that contains context on the species group as a whole.
     * @param subSpeciesByRegion Individual PokemonDTO of species group mapped by region. A Pokemon can have only one default regional
     *                           form variant per region.
     * @return The records entries bundled.
     */
    public static PokemonEntriesBundle fromSpecies(PokemonSpeciesDTO baseSpecies, EnumMap<RegionalForm, PokemonDTO> subSpeciesByRegion) {
        Objects.requireNonNull(baseSpecies);
        Objects.requireNonNull(subSpeciesByRegion);

        if (subSpeciesByRegion.isEmpty())
            throw new IllegalArgumentException("RegionalForm.STANDARD value at very least expected in passed map, but no keys present");

        List<PokemonEntry> pokemonEntries = new ArrayList<>();
        List<PokemonSpriteEntry> pokemonSpriteEntries = new ArrayList<>();

        for (RegionalForm regionForm : subSpeciesByRegion.keySet()) {
            PokemonDTO pokemonOfSpecies = subSpeciesByRegion.get(regionForm);

            if (pokemonOfSpecies == null)
                throw new NullPointerException("Value null for regional key: " + regionForm);

            pokemonEntries
                    .add(PokemonEntry.from(pokemonOfSpecies, baseSpecies));

            boolean isCurrPokemonFemaleSpritePresent = pokemonOfSpecies.femaleSpriteLink() != null;

            /*
            Both parts of boolean are needed.
            >Blissey has a female sprite, but no sprite gender differences
            as it is all female Pokemon.
            >Alolan Raticate has a female sprite, but its species is marked
            as having gender differences as original/base form of Raticate
            does have gender differences in its group.

            hasGenderDifferences is a bit misleading as it is not
            case for all pokemon that if the "species" has gender differences that all
            "subspecies" do too as previously thought. It does garuantee that standard non-regional variant Pokemon
            has gender differences though. For all female species program should put GenderSpriteForm.NONE, since the
            female sprite represents the universal default form of the sprite and there are no gender differences.

            In short, this check is to create two entries for male and female sprites of current Pokemon only if
            this Pokemon has gender diffs present between its two genders. A Pokemon with no gender or unknown gender
            has no gender differences in its group so it goes to else block, Pokemon with all male or female groups go else
            block as well for same reason.
            */
            if (baseSpecies.hasGenderDifferences() && isCurrPokemonFemaleSpritePresent) {
                pokemonSpriteEntries
                        .add(new PokemonSpriteEntry(pokemonOfSpecies.id(), GenderSpriteForm.MALE, regionForm, baseSpecies.dexId()));
                pokemonSpriteEntries
                        .add(new PokemonSpriteEntry(pokemonOfSpecies.id(), GenderSpriteForm.FEMALE, regionForm, baseSpecies.dexId()));
            } else {
                pokemonSpriteEntries
                        .add(new PokemonSpriteEntry(pokemonOfSpecies.id(), GenderSpriteForm.NONE, regionForm, baseSpecies.dexId()));
            }
        }

        return new PokemonEntriesBundle(pokemonEntries, pokemonSpriteEntries);
    }

    /**
     * Flattens (reduces) a list of PokemonEntriesBundle, typically representing bundled entries of each Pokemon from a single generation,
     * into one PokemonEntriesBundle with unmodifable list contents.
     * @param pokemonBundles List of PokemonEntriesBundle instances that will have contents reduced to single PokemonEntriesBundle. This
     *                       list is not altered or modified by method.
     * @return PokemonEntriesBundle - Flattened result, with all PokemonEntry and PokemonSpriteEntry instances combined into one
     * unmodifiable bundle
     */
    public static PokemonEntriesBundle flattenBundles(List<PokemonEntriesBundle> pokemonBundles) {
        List<PokemonEntry> generationPokemonEntries = new ArrayList<>();
        List<PokemonSpriteEntry> generationPokemonSpriteEntries = new ArrayList<>();

        for (PokemonEntriesBundle bundle : pokemonBundles) {
            generationPokemonEntries.addAll(bundle.pokemonEntries());
            generationPokemonSpriteEntries.addAll(bundle.pokemonSpriteEntries());
        }

        return new PokemonEntriesBundle(
                List.copyOf(generationPokemonEntries),
                List.copyOf(generationPokemonSpriteEntries)
        );
    }

}
