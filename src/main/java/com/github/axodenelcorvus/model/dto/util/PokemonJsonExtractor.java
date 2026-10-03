package com.github.axodenelcorvus.model.dto.util;

import com.github.axodenelcorvus.model.dto.PokemonDTO;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;


class PokemonJsonExtractor implements PokeApiJsonBodyExtractor<PokemonDTO> {

    public PokemonDTO extractFrom(JsonNode root) {
        int id = root.get("id").asInt();
        String primaryType;
        String secondaryType = null;
        String pokemonSlugName = root.get("name").asString();

        ArrayNode typesSlots = root.get("types").asArray();
        primaryType = typesSlots.get(0)
                .get("type").get("name").asString();

        boolean bothTypesAvailable = typesSlots.size() == 2;

        if (bothTypesAvailable) {
            secondaryType = typesSlots.get(1)
                    .get("type").get("name").asString();
        }

        return new PokemonDTO(id, pokemonSlugName, primaryType, secondaryType);
    }

}
