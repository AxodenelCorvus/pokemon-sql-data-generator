package com.github.axodenelcorvus.dto.pokemon_species;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.axodenelcorvus.dto.PokeApiDto;

import java.util.List;

public record PokemonSpeciesDTO(
        int dexID,
        boolean hasGenderDifferences,
        int genderRate,
        @JsonProperty("name") String englishName,
        boolean isLegendary,
        boolean isMythical,
        List<SpeciesVariety> varieties

) implements PokeApiDto { }
