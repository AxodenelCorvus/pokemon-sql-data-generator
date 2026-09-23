package com.github.axodenelcorvus;

import com.github.axodenelcorvus.entry.PokemonEntry;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntriesTest {

    @Test
    void checkPokemonEntryDeserializableToTuple() {

        String expectedTupleStr = "(199, 199, 'Slowking', false, 4, 'Water', 'Psychic', false, false)";

        PokemonEntry testPikachuEntry = new PokemonEntry(
                199,
                199,
                "Slowking",
                false,
                4,
                "Water",
                "Psychic",
                false,
                false
        );

        String actualTupleStrRepresentation = testPikachuEntry.toSqlTuple();
        assertEquals(expectedTupleStr, actualTupleStrRepresentation);
    }

}
