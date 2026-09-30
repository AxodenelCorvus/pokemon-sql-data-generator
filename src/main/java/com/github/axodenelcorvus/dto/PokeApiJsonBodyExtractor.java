package com.github.axodenelcorvus.dto;

import tools.jackson.databind.JsonNode;


public interface PokeApiJsonBodyExtractor<T extends PokeApiDto> {
    /**
     * Extracts from a Poke API JSON body in memory the necessary data to load
     * into PokeApiDto before returning.
     * @param root  A tools.jackson.databind.JsonNode that represents JSON tree that should correspond with PokeApiDto return type
     * @return A PokeApiDto marked object that has been loaded with correctly mapped values
     */
    T extractFrom(JsonNode root);
}
