package com.github.axodenelcorvus.dto.pokemon_species;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpeciesVarietyDTO(
        boolean isDefault,
        @JsonProperty("pokemon") PokemonResourceDTO pokemonResource) { }
