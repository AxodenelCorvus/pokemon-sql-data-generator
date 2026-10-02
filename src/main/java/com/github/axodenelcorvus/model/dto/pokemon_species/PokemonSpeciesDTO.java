package com.github.axodenelcorvus.model.dto.pokemon_species;

import com.github.axodenelcorvus.model.dto.PokeApiDto;

import java.util.List;

public record PokemonSpeciesDTO(
        int dexId,
        boolean hasGenderDifferences,
        int genderRate,
        String name,
        boolean isLegendary,
        boolean isMythical,
        List<PokemonVarietyDTO> varieties) implements PokeApiDto { }
