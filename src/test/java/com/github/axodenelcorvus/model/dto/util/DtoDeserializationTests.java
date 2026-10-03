package com.github.axodenelcorvus.model.dto.util;

import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;
import com.github.axodenelcorvus.model.dto.PokemonVarietyDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DtoDeserializationTests {
    private final PokeApiJsonParser pokeApiJsonParser =
                                            new PokeApiJsonParser();

    private static Path BASE_PATH = Path.of("src", "test", "resources");

    private static String readLinesFromFile(Path pathToSample) throws IOException {
        try (BufferedReader fReader = Files.newBufferedReader(pathToSample)) {
            return fReader.lines().collect(Collectors.joining());
        }
    }

    @Test
    @DisplayName("PokemonDTO deserializes correctly using JsonNode from Jackson dependency")
    void testDeserialize() throws IOException {
        Path endPath = Path.of("pokemon", "BulbasaurSample.json");
        String bulbasaurSampleJSON = readLinesFromFile(BASE_PATH.resolve(endPath));

        PokemonDTO actualDTO = pokeApiJsonParser.parsePokemonBody(bulbasaurSampleJSON);

        assertEquals(1, actualDTO.id());
        assertEquals("bulbasaur", actualDTO.slugName());
        assertEquals("grass", actualDTO.primaryType());
        assertEquals("poison", actualDTO.secondaryType());
    }

    @Test
    @DisplayName("PokemonDTO deserializes correctly when 2nd type is absent")
    void testDeserializeAbsentSecondaryType() throws IOException {
        Path endPath = Path.of("pokemon", "CorsolaSample.json");
        String corsolaSampleJSON = readLinesFromFile(BASE_PATH.resolve(endPath));
        PokemonDTO actualDTO = pokeApiJsonParser.parsePokemonBody(corsolaSampleJSON);

        assertEquals(10173, actualDTO.id());
        assertEquals("corsola-galar", actualDTO.slugName());
        assertEquals("ghost", actualDTO.primaryType());
        assertNull(actualDTO.secondaryType());
    }

    @Test
    @DisplayName("Deserialize Sneasel JSON sample")
    void deserializeSneaselSample() throws IOException {
        Path endPath = Path.of("pokemon_species", "SneaselSample.json");
        String sneaselSampleJSON = readLinesFromFile(BASE_PATH.resolve(endPath));
        PokemonSpeciesDTO actualDTO = pokeApiJsonParser.parsePokemonSpeciesBody(sneaselSampleJSON);

        List<PokemonVarietyDTO> expectedVarietiesList = List.of(
                new PokemonVarietyDTO(
                        true,
                        "sneasel"),
                new PokemonVarietyDTO(
                        false,
                        "sneasel-hisui")
        );

        PokemonSpeciesDTO expectedDTO = new PokemonSpeciesDTO(
                215,
                true,
                4,
                "Sneasel",
                false,
                false,
                expectedVarietiesList
        );

        assertEquals(expectedDTO, actualDTO);
    }

}
