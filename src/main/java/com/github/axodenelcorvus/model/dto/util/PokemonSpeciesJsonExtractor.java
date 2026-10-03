package com.github.axodenelcorvus.model.dto.util;

import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;
import com.github.axodenelcorvus.model.dto.PokemonVarietyDTO;
import tools.jackson.databind.JsonNode;

import java.util.Collection;
import java.util.List;

class PokemonSpeciesJsonExtractor implements PokeApiJsonBodyExtractor<PokemonSpeciesDTO> {
    private final VarietyJsonExtractor varietyJsonExtractor = new VarietyJsonExtractor();

    //This scan exists because it can obtain the capitalized name
    //with special characters. Example: Nidoran♂
    private String findEnglishName(Collection<JsonNode> namesNode) {
        String englishName = "";

        for (JsonNode nameNode : namesNode) {
            var languageCode = nameNode.get("language").get("name").asString();
            if (languageCode.equalsIgnoreCase("en")) {
                englishName = nameNode.get("name").asString();
                break;
            }
        }

        return englishName;
    }

    public PokemonSpeciesDTO extractFrom(JsonNode root) {
        int dexId = root.get("id").asInt();
        boolean hasGenderDifferences = root.get("has_gender_differences").asBoolean();
        int genderRate = root.get("gender_rate").asInt();
        boolean isLegendary = root.get("is_legendary").asBoolean();
        boolean isMythical = root.get("is_mythical").asBoolean();

        Collection<JsonNode> namesNode = root.get("names")
                                    .asArray()
                                    .elements();
        Collection<JsonNode> varietiesNode = root.get("varieties")
                                    .asArray()
                                    .elements();

        String name = findEnglishName(namesNode);

        List<PokemonVarietyDTO> varieties = varietiesNode
                .stream()
                .map(varietyJsonExtractor::extractFrom)
                .toList();

        return new PokemonSpeciesDTO(dexId, hasGenderDifferences, genderRate, name, isLegendary, isMythical, varieties);
    }

}
