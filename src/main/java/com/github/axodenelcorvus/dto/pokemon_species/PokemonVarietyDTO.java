package com.github.axodenelcorvus.dto.pokemon_species;

import com.github.axodenelcorvus.dto.PokeApiDto;

import java.net.URI;

public record PokemonVarietyDTO(
        boolean isDefault,
        URI speciesLink) implements PokeApiDto { }
