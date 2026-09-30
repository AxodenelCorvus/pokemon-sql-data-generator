package com.github.axodenelcorvus.dto.pokemon_species;

import com.github.axodenelcorvus.dto.PokeApiDto;

import java.util.List;

public record PokemonSpeciesDTO(
        int dexId,
        boolean hasGenderDifferences,
        int genderRate,
        String name,
        boolean isLegendary,
        boolean isMythical,
        List<PokemonVarietyDTO> varieties) implements PokeApiDto { }
