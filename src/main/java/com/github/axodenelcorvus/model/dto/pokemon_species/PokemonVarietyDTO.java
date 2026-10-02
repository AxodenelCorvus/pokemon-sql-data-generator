package com.github.axodenelcorvus.model.dto.pokemon_species;

import com.github.axodenelcorvus.model.dto.PokeApiDto;

public record PokemonVarietyDTO(boolean isDefault,
                                String slugName) implements PokeApiDto { }
