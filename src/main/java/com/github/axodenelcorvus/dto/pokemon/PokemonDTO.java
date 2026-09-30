package com.github.axodenelcorvus.dto.pokemon;

import com.github.axodenelcorvus.dto.PokeApiDto;

public record PokemonDTO(int id, String primaryType, String secondaryType) implements PokeApiDto { }
