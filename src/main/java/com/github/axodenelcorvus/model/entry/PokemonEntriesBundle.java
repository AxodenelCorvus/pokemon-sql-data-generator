package com.github.axodenelcorvus.model.entry;

import java.util.List;

public record PokemonEntriesBundle(
        List<PokemonEntry> pokemonEntries,
        List<PokemonSpriteEntry> pokemonSpriteEntries) { }
