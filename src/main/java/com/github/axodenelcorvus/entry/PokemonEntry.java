package com.github.axodenelcorvus.entry;

public record PokemonEntry(int id,
                           int nationalPokeDexId,
                           String name,
                           boolean hasGenderDifferences,
                           Integer genderRate,
                           String primaryType,
                           String secondaryType,
                           boolean isMythical,
                           boolean isLegendary) implements SqlTupleSerializable {

    public PokemonEntry {
        if (genderRate < 0) {
            genderRate = null;
        }
    }

    public String toSqlTuple() {
        String sqlTupleResult = "(%d, %d, '%s', %b, %d, '%s', '%s', %b, %b)";

        return String.format(sqlTupleResult,
                id,
                nationalPokeDexId,
                name,
                hasGenderDifferences,
                genderRate,
                primaryType,
                secondaryType,
                isMythical,
                isLegendary
        );
    }
}
