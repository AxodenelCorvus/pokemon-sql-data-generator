package com.github.axodenelcorvus.model.dto;

public record PokemonDTO(
        int id,
        String slugName,
        String primaryType,
        String secondaryType,
        String femaleSpriteLink
) implements PokeApiDto { }
