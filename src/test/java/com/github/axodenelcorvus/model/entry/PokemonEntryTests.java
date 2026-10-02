package com.github.axodenelcorvus.model.entry;

import com.github.axodenelcorvus.model.dto.DtoSampleProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PokemonEntryTests {
    private final SqlStringResolver sqlStrResolver = new SqlStringResolver();
    private final DtoSampleProvider dtoProvider = new DtoSampleProvider();


    @Test
    @DisplayName("Test a standard entry")
    void testStandardPokemonEntrySerializableToTuple() {
        String expectedTupleStr = "(80, 80, 'Slowbro', 4, 'Water', 'Psychic', false, false)";
        var testSlowbroEntry = PokemonEntry.from(dtoProvider.getPokemonDTO(80), dtoProvider.getPokemonSpeciesDTO(80));

        String actualTupleStrRepresentation = testSlowbroEntry.toSqlTuple(sqlStrResolver);
        assertEquals(expectedTupleStr, actualTupleStrRepresentation);
    }

    @Test
    @DisplayName("Test galarian Farfetch'd sample and escaping of apostrophe")
    void testGalarianPokemonWithApostropheName() {
        String expectedTupleStr = "(10166, 83, 'Farfetch''d', 4, 'Fighting', null, false, false)";

        var testFarfetchdEntry = PokemonEntry.from(dtoProvider.getPokemonDTO(10166), dtoProvider.getPokemonSpeciesDTO(10166));

        String actualTupleStrRepresentation = testFarfetchdEntry.toSqlTuple(sqlStrResolver);
        assertEquals(expectedTupleStr, actualTupleStrRepresentation);
    }

    @Test
    @DisplayName("Test a Pokemon with unknown gender (negative gender rate)")
    void testPokemonUnknownGender() {
        String expectedTupleStr = "(151, 151, 'Mew', null, 'Psychic', null, true, false)";
        var testMewEntry = PokemonEntry.from(dtoProvider.getPokemonDTO(151), dtoProvider.getPokemonSpeciesDTO(151));

        String actualTupleStrRepresentation = testMewEntry.toSqlTuple(sqlStrResolver);
        assertEquals(expectedTupleStr, actualTupleStrRepresentation);
    }

}
