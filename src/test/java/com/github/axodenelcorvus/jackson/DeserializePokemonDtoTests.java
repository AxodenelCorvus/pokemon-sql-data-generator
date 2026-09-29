package com.github.axodenelcorvus.jackson;

import com.github.axodenelcorvus.dto.PokeApiJsonBodyExtractor;
import com.github.axodenelcorvus.dto.pokemon.PokemonDTO;
import com.github.axodenelcorvus.dto.pokemon.PokemonJsonExtractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeserializePokemonDtoTests {
    private final ObjectMapper jacksonMapper = new ObjectMapper();

    //Most data omitted compared to what API actually returns
    static final String jsonPokemonSample1 = """
    {
      "id": 1,
      "name": "bulbasaur",
    
      "types": [
        {
          "slot": 1,
          "type": {
            "name": "grass"
          }
        },
    
        {
    
          "slot": 2,
          "type": {
           "name": "poison"
          }
        }
      ]
    
    }
    """;

    static  final String jsonPokemonSample2 = """
    {
      "id": 132,
      "name": "ditto",
    
      "types": [
        {
          "slot": 1,
          "type": {
            "name": "normal"
          }
        }
      ]
    
    }
    """;

    @Test
    @DisplayName("Does PokemonDTO deserialize correctly using JsonNode from Jackson dependency")
    void testDeserialize() {
        JsonNode jsonBody = jacksonMapper.readTree(jsonPokemonSample1);
        PokeApiJsonBodyExtractor<PokemonDTO> pokemonJsonBodyExtractor = new PokemonJsonExtractor();
        PokemonDTO dto = pokemonJsonBodyExtractor.extractFrom(jsonBody);

        assertEquals(1, dto.ID());
        assertEquals("grass", dto.primaryType());
        assertEquals("poison", dto.secondaryType());
    }

    @Test
    @DisplayName("Does PokemonDTO deserialize correctly using")
    void testDeserializeAbsentSecondaryType() {
        JsonNode jsonBody = jacksonMapper.readTree(jsonPokemonSample2);
        PokeApiJsonBodyExtractor<PokemonDTO> pokemonJsonBodyExtractor = new PokemonJsonExtractor();
        PokemonDTO dto = pokemonJsonBodyExtractor.extractFrom(jsonBody);

        assertEquals(132, dto.ID());
        assertEquals("normal", dto.primaryType());
        assertNull(dto.secondaryType());
    }

}
