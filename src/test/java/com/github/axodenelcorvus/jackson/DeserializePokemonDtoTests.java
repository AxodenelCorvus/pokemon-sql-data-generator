package com.github.axodenelcorvus.jackson;

import com.github.axodenelcorvus.dto.PokeApiJsonBodyExtractor;
import com.github.axodenelcorvus.dto.pokemon.PokemonDTO;
import com.github.axodenelcorvus.dto.pokemon.PokemonJsonExtractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeserializePokemonDtoTests {
    private final ObjectMapper jacksonMapper = new ObjectMapper();

    @Test
    @DisplayName("Does PokemonDTO deserialize correctly using JsonNode from Jackson dependency")
    void testDeserialize() {
        JsonNode jsonBody = jacksonMapper.readTree(new File("src/test/resources/BulbasaurSample.json"));
        PokeApiJsonBodyExtractor<PokemonDTO> pokemonJsonBodyExtractor = new PokemonJsonExtractor();
        PokemonDTO dto = pokemonJsonBodyExtractor.extractFrom(jsonBody);

        assertEquals(1, dto.id());
        assertEquals("grass", dto.primaryType());
        assertEquals("poison", dto.secondaryType());
    }

    @Test
    @DisplayName("Does PokemonDTO deserialize correctly when 2nd type is absent")
    void testDeserializeAbsentSecondaryType() {
        JsonNode jsonBody = jacksonMapper.readTree(new File("src/test/resources/DittoSample.json"));
        PokeApiJsonBodyExtractor<PokemonDTO> pokemonJsonBodyExtractor = new PokemonJsonExtractor();
        PokemonDTO dto = pokemonJsonBodyExtractor.extractFrom(jsonBody);

        assertEquals(132, dto.id());
        assertEquals("normal", dto.primaryType());
        assertNull(dto.secondaryType());
    }

}
