package com.github.axodenelcorvus.dto.pokemon_species;

import com.github.axodenelcorvus.dto.PokeApiDto;

public record PokemonVarietyDTO(
        boolean isDefault,
        String slugName) implements PokeApiDto { }
