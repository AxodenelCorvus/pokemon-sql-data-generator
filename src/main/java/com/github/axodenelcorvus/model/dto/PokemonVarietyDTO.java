package com.github.axodenelcorvus.model.dto;

public record PokemonVarietyDTO(boolean isDefault,
                                String slugName) implements PokeApiDto { }
