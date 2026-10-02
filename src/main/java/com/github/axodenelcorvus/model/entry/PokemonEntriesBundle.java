package com.github.axodenelcorvus.model.entry;

import java.util.List;

//TODO review usage/need later
public record PokemonEntriesBundle(
        List<PokemonEntry> pokemonEntries,
        List<PokemonSpriteEntry> pokemonSpriteEntries) { }
