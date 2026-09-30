package com.github.axodenelcorvus.jackson;

import com.github.axodenelcorvus.dto.pokemon_species.PokemonSpeciesDTO;
import com.github.axodenelcorvus.dto.pokemon_species.PokemonSpeciesJsonExtractor;
import com.github.axodenelcorvus.dto.pokemon_species.PokemonVarietyDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeserializePokemonSpeciesDtoTests {
    private final ObjectMapper jacksonMapper = new ObjectMapper();

    @Test
    @DisplayName("Deserialize Sneasel JSON sample")
    void deserializeSneaselSample() {
        var speciesExtractor = new PokemonSpeciesJsonExtractor();

        JsonNode jsonBody = jacksonMapper
                .readTree(new File("src/test/resources/SneaselSample.json"));

        PokemonSpeciesDTO pokemonSpeciesDTO = speciesExtractor.extractFrom(jsonBody);

        List<PokemonVarietyDTO> varietiesListDTO = List.of(
                new PokemonVarietyDTO(
                        true,
                        "sneasel"),
                new PokemonVarietyDTO(
                        false,
                        "sneasel-hisui")
        );

        PokemonSpeciesDTO expectedSpeciesDTO = new PokemonSpeciesDTO(
                215,
                true,
                4,
                "Sneasel",
                false,
                false,
                varietiesListDTO
        );

        assertEquals(expectedSpeciesDTO, pokemonSpeciesDTO);

    }

}
