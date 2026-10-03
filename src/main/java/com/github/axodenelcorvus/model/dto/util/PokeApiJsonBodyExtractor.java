package com.github.axodenelcorvus.model.dto.util;

import com.github.axodenelcorvus.model.dto.PokeApiDto;
import tools.jackson.databind.JsonNode;


interface PokeApiJsonBodyExtractor<T extends PokeApiDto> {
    /**
     * Extracts from a Poke API JSON body in memory the necessary data to load
     * into a PokeApiDto object before returning.
     * @param root  A tools.jackson.databind.JsonNode that represents JSON tree that should correspond with PokeApiDto return type
     * @return A PokeApiDto marked object that has been loaded with correctly mapped values
     */
    T extractFrom(JsonNode root);
}
