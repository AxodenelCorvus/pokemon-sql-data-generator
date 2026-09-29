package com.github.axodenelcorvus.dto;

import tools.jackson.databind.JsonNode;


public interface PokeApiJsonBodyExtractor<T extends PokeApiDto> {
    /**
     * Extracts from a Poke API JSON body in memory the data that is needed. Afterwards, only necessary extracted data gets loaded
     * into PokeApiDto and returned.
     * @param root  A tools.jackson.databind.JsonNode that represents JSON tree that should correspond with Loadable return type
     * @return A PokeApiDataLoadable marked object that has been loaded with correctly mapped values
     */
    T extractFrom(JsonNode root);
}
