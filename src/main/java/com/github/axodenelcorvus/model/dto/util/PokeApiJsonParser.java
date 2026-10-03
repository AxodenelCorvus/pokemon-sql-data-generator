package com.github.axodenelcorvus.model.dto.util;

import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

public class PokeApiJsonParser {
    private final PokemonJsonExtractor pokemonExtractor = new PokemonJsonExtractor();
    private final PokemonSpeciesJsonExtractor speciesExtractor = new PokemonSpeciesJsonExtractor();
    private final ObjectMapper mapper;

    public PokeApiJsonParser() {
        //Can configure any changes to mapper here, it is only used by this object directly
        this.mapper = new ObjectMapper();
    }

    public PokemonDTO parsePokemonBody(String json) {
        JsonNode root = mapper.readTree(json);
        return pokemonExtractor.extractFrom(root);
    }

    public PokemonSpeciesDTO parsePokemonSpeciesBody(String speciesJsonBody) {
        JsonNode root = mapper.readTree(speciesJsonBody);
        return speciesExtractor.extractFrom(root);
    }

    public List<String> parseSpeciesSlugNames(String generationJsonBody) {
        JsonNode generationBody = mapper.readTree(generationJsonBody);

        List<JsonNode> nameNodes = generationBody
                .get("pokemon_species")
                .asArray()
                .findValues("name");

        return nameNodes
                .stream()
                .map(JsonNode::asString)
                .toList();
    }

}
