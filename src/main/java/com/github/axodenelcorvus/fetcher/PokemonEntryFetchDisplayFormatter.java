package com.github.axodenelcorvus.fetcher;

import com.github.axodenelcorvus.model.entry.PokemonEntry;

final class PokemonEntryFetchDisplayFormatter {
    private static final int MAX_TYPE_WIDTH = 8; //Such as "Fighting" type Pokemon
    private static final int MAX_DEX_ID_WIDTH = 4;

    private PokemonEntryFetchDisplayFormatter() {}

    /*
    Since normal non-regional Pokemon are in thousands range, variants (both regionals and other forms)
    are in tens-thousand place. There is no overlap with Dex ID's and so valid future Dex ID's can use
    valid values in thousands place not yet taken.
    */
    private static final int MAX_POKE_API_POKEMON_ID = 5;

    //Goal of field is to keep all fields same length in display
    private static final String DISPLAY_FORMAT = "Pokemon Entry Collected) "
            + " Primary Key: %-" + MAX_POKE_API_POKEMON_ID + 'd'
            + " | Dex ID: %0" + MAX_DEX_ID_WIDTH + 'd'
            + " | Type1: %-" + MAX_TYPE_WIDTH + 's'
            + " | Type2: %s";

    public static String formatDisplayFrom(PokemonEntry pokemonEntry) {
        String secondaryTypeDisplayVal = pokemonEntry.getSecondaryType();

        if (secondaryTypeDisplayVal == null)
            secondaryTypeDisplayVal = "N/A";

        return DISPLAY_FORMAT.formatted(
                pokemonEntry.getId(),
                pokemonEntry.getNationalDexId(),
                pokemonEntry.getPrimaryType(),
                secondaryTypeDisplayVal
        );
    }
}