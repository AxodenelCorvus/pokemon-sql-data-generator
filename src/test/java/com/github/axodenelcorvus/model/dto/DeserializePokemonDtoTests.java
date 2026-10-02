package com.github.axodenelcorvus.model.dto;

import com.github.axodenelcorvus.model.dto.pokemon.PokemonDTO;
import com.github.axodenelcorvus.model.dto.pokemon.PokemonJsonExtractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeserializePokemonDtoTests {
    private final ObjectMapper jacksonMapper = new ObjectMapper();

    @Test
    @DisplayName("Does PokemonDTO deserialize correctly using JsonNode from Jackson dependency")
    void testDeserialize() {
        JsonNode jsonBody = jacksonMapper.readTree(new File("src/test/resources/pokemon/BulbasaurSample.json"));
        PokeApiJsonBodyExtractor<PokemonDTO> pokemonJsonBodyExtractor = new PokemonJsonExtractor();
        PokemonDTO dto = pokemonJsonBodyExtractor.extractFrom(jsonBody);

        assertEquals(1, dto.id());
        assertEquals("bulbasaur", dto.slugName());
        assertEquals("grass", dto.primaryType());
        assertEquals("poison", dto.secondaryType());
    }

    @Test
    @DisplayName("Does PokemonDTO deserialize correctly when 2nd type is absent")
    void testDeserializeAbsentSecondaryType() {
        JsonNode jsonBody = jacksonMapper.readTree(new File("src/test/resources/pokemon/CorsolaSample.json"));
        PokeApiJsonBodyExtractor<PokemonDTO> pokemonJsonBodyExtractor = new PokemonJsonExtractor();
        PokemonDTO dto = pokemonJsonBodyExtractor.extractFrom(jsonBody);

        assertEquals(10173, dto.id());
        assertEquals("corsola-galar", dto.slugName());
        assertEquals("ghost", dto.primaryType());
        assertNull(dto.secondaryType());
    }

}
