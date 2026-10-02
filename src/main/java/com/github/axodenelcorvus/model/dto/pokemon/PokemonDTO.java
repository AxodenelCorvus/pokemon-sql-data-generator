package com.github.axodenelcorvus.model.dto.pokemon;

import com.github.axodenelcorvus.model.dto.PokeApiDto;

public record PokemonDTO(int id, String slugName, String primaryType, String secondaryType) implements PokeApiDto { }
