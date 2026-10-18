package com.github.axodenelcorvus.model.dto.util;

import com.github.axodenelcorvus.model.dto.PokemonDTO;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;


class PokemonJsonExtractor implements PokeApiJsonBodyExtractor<PokemonDTO> {
    private static final int PRIMARY_TYPE_INDEX = 0;
    private static final int SECONDARY_TYPE_INDEX = 1;

    public PokemonDTO extractFrom(JsonNode root) {
        int id = root.get("id").asInt();
        String primaryType;
        String secondaryType = null;
        String pokemonSlugName = root.get("name").asString();

        //This path will always have an up to date and available sprite
        //for every Pokemon for forseeable future
        JsonNode femaleSpriteLinkNode = root
                .get("sprites")
                    .get("other")
                        .get("home")
                            .get("front_female");

        String femaleSpriteLink = null;

        if (!femaleSpriteLinkNode.isNull())
            femaleSpriteLink = femaleSpriteLinkNode.asString();

        ArrayNode typesSlots = root.get("types").asArray();

        primaryType = typesSlots.get(PRIMARY_TYPE_INDEX)
                .get("type")
                    .get("name").asString();

        boolean bothTypesAvailable = typesSlots.size() == 2;

        if (bothTypesAvailable) {
            secondaryType = typesSlots.get(SECONDARY_TYPE_INDEX)
                    .get("type")
                        .get("name").asString();
        }

        return new PokemonDTO(id, pokemonSlugName, primaryType, secondaryType, femaleSpriteLink);
    }

}
