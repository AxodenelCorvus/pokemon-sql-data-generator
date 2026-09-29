package com.github.axodenelcorvus.dto.pokemon_species;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;

//Points to Poke API pokemon endpoint resource
public record PokemonResource(
        @JsonProperty("name") String slugName,
        URI url) { }
