package com.github.axodenelcorvus.dto.pokemon;

import com.github.axodenelcorvus.dto.PokeApiJsonBodyExtractor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;


public class PokemonJsonExtractor implements PokeApiJsonBodyExtractor<PokemonDTO> {

    public PokemonDTO extractFrom(JsonNode root) {
        int id = root.get("id").asInt();
        String primaryType;
        String secondaryType = null;

        ArrayNode typesSlots = root.get("types").asArray();
        primaryType = typesSlots.get(0)
                .get("type").get("name").asString();

        boolean bothTypesAvailable = typesSlots.size() == 2;

        if (bothTypesAvailable) {
            secondaryType = typesSlots.get(1)
                    .get("type").get("name").asString();
        }

        return new PokemonDTO(id, primaryType, secondaryType);
    }

}
