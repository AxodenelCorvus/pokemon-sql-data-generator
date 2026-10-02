package com.github.axodenelcorvus.model.dto.pokemon_species;

import com.github.axodenelcorvus.model.dto.PokeApiJsonBodyExtractor;
import tools.jackson.databind.JsonNode;


public class VarietyJsonExtractor implements PokeApiJsonBodyExtractor<PokemonVarietyDTO> {

    public PokemonVarietyDTO extractFrom(JsonNode root) {
        var isDefault = root.get("is_default").asBoolean();
        var pokemonResourceSlugName = root.get("pokemon").get("name").asString();

        return new PokemonVarietyDTO(isDefault, pokemonResourceSlugName);
    }

}
