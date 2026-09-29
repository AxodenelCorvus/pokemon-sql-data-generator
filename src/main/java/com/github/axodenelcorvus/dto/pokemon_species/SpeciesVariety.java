package com.github.axodenelcorvus.dto.pokemon_species;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpeciesVariety(
        boolean isDefault,
        @JsonProperty("pokemon") PokemonResource pokemonResource) { }
