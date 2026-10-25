package com.github.axodenelcorvus.model.entry;

import com.github.axodenelcorvus.model.DtoSampleProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PokemonEntryTests {
    private final SqlStringResolver sqlStrResolver = new SqlStringResolver();
    private final DtoSampleProvider dtoProvider = new DtoSampleProvider();

    //Since all Pokemon in tests use a generation one introduced Pokemon species
    private final FossilPokemonChecker genOneFossilChecker = new FossilPokemonChecker(1);

    @Test
    @DisplayName("Test a standard entry")
    void testStandardPokemonEntrySerializableToTuple() {
        String expectedTupleStr = "(80, 80, 'Slowbro', 4, 'Water', 'Psychic', false, false, false)";
        var testSlowbroEntry = PokemonEntry.from(
                dtoProvider.getPokemonDTO(80),
                dtoProvider.getPokemonSpeciesDTO(80),
                genOneFossilChecker
        );

        String actualTupleStrRepresentation = testSlowbroEntry.toSqlTuple(sqlStrResolver);
        assertEquals(expectedTupleStr, actualTupleStrRepresentation);
    }

    @Test
    @DisplayName("Test galarian Farfetch'd sample and escaping of apostrophe")
    void testGalarianPokemonWithApostropheName() {
        String expectedTupleStr = "(10166, 83, 'Farfetch''d', 4, 'Fighting', null, false, false, false)";
        var testFarfetchdEntry = PokemonEntry.from(
                dtoProvider.getPokemonDTO(10166),
                dtoProvider.getPokemonSpeciesDTO(10166),
                genOneFossilChecker
        );

        String actualTupleStrRepresentation = testFarfetchdEntry.toSqlTuple(sqlStrResolver);
        assertEquals(expectedTupleStr, actualTupleStrRepresentation);
    }

    @Test
    @DisplayName("Test a Pokemon with unknown gender (negative gender rate)")
    void testPokemonUnknownGender() {
        String expectedTupleStr = "(151, 151, 'Mew', null, 'Psychic', null, true, false, false)";
        var testMewEntry = PokemonEntry.from(
                dtoProvider.getPokemonDTO(151),
                dtoProvider.getPokemonSpeciesDTO(151),
                genOneFossilChecker
        );

        String actualTupleStrRepresentation = testMewEntry.toSqlTuple(sqlStrResolver);
        assertEquals(expectedTupleStr, actualTupleStrRepresentation);
    }

}
