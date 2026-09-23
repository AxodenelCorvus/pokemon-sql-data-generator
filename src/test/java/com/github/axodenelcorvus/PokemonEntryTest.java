package com.github.axodenelcorvus;

import com.github.axodenelcorvus.entry.PokemonEntry;
import com.github.axodenelcorvus.entry.SqlStringResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PokemonEntryTest {
    private final SqlStringResolver sqlStrResolver = new SqlStringResolver();

    @Test
    void testStandardPokemonEntryDeserializableToTuple() {
        String expectedTupleStr = "(199, 199, 'Slowking', false, 4, 'Water', 'Psychic', false, false)";

        PokemonEntry testSlowkingEntry = new PokemonEntry(
                199,
                199,
                "Slowking",
                4,
                "Water",
                "Psychic",
                false,
                false
        );

        String actualTupleStrRepresentation = testSlowkingEntry.toSqlTuple(sqlStrResolver);
        assertEquals(expectedTupleStr, actualTupleStrRepresentation);
    }

    @Test
    void testGalarianPokemonWithApostropheName() {
        String expectedTupleStr = "(10166, 83, 'Farfetch''d', false, 4, 'Fighting', null, false, false)";
        PokemonEntry testFarfetchdEntry = new PokemonEntry(
                10166,
                83,
                "Farfetch'd",
                4,
                "Fighting",
                null,
                false,
                false
        );

        String actualTupleStrRepresentation = testFarfetchdEntry.toSqlTuple(sqlStrResolver);
        assertEquals(expectedTupleStr, actualTupleStrRepresentation);
    }

    @Test
    void testPokemonUnknownGender() {
        String expectedTupleStr = "(716, 716, 'Xerneas', false, null, 'Fairy', null, false, true)";
        PokemonEntry testFarfetchdEntry = new PokemonEntry(
                716,
                716,
                "Xerneas",
                -1,
                "Fairy",
                null,
                false,
                true
        );

        String actualTupleStrRepresentation = testFarfetchdEntry.toSqlTuple(sqlStrResolver);
        assertEquals(expectedTupleStr, actualTupleStrRepresentation);
    }

}
