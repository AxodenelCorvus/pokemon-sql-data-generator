package com.github.axodenelcorvus.dto.pokemon_species;

import com.github.axodenelcorvus.dto.PokeApiJsonBodyExtractor;
import tools.jackson.databind.JsonNode;

import java.net.URI;

public class VarietyJsonExtractor implements PokeApiJsonBodyExtractor<PokemonVarietyDTO> {

    public PokemonVarietyDTO extractFrom(JsonNode root) {
        var isDefault = root.get("is_default").asBoolean();
        var pokemonResourceURL = root.get("pokemon").get("url").asString();

        return new PokemonVarietyDTO(isDefault, URI.create(pokemonResourceURL));
    }

}
