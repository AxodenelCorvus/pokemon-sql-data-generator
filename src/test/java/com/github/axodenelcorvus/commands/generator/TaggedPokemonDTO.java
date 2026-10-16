package com.github.axodenelcorvus.commands.generator;

import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.entry.RegionalForm;

import java.util.Objects;

record TaggedPokemonDTO(PokemonDTO value, RegionalForm regionalTag) {
    TaggedPokemonDTO {
        //To help in failing fast for tests, if ever needed
        Objects.requireNonNull(value);
        Objects.requireNonNull(regionalTag);
    }
}
