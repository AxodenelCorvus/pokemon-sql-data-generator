package com.github.axodenelcorvus.model.dto.util;

import com.github.axodenelcorvus.model.dto.PokemonVarietyDTO;
import tools.jackson.databind.JsonNode;


class VarietyJsonExtractor implements PokeApiJsonBodyExtractor<PokemonVarietyDTO> {

    public PokemonVarietyDTO extractFrom(JsonNode root) {
        var isDefault = root.get("is_default").asBoolean();
        var pokemonResourceSlugName = root.get("pokemon").get("name").asString();

        return new PokemonVarietyDTO(isDefault, pokemonResourceSlugName);
    }

}
