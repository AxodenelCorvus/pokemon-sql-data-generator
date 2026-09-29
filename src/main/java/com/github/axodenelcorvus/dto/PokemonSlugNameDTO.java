package com.github.axodenelcorvus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

//Wrapper
public record PokemonSlugNameDTO(@JsonProperty("name") String slugName) { }
