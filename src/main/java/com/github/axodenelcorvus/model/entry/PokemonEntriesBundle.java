package com.github.axodenelcorvus.model.entry;

import java.util.ArrayList;
import java.util.List;

public record PokemonEntriesBundle(
        List<PokemonEntry> pokemonEntries,
        List<PokemonSpriteEntry> pokemonSpriteEntries)
{

    /**
     * Flattens (reduces) a large list of PokemonEntriesBundle, typically representing bundled entries of each Pokemon from a single generation,
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
