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


    public String toSqlTuple(SqlStringResolver sqlStrResolution) {
        return  "(%d, %d, %s, %b, %d, %s, %s, %b, %b)".formatted(
                id,
                nationalPokeDexId,
                sqlStrResolution.apply(name),
                hasGenderDifferences,
                genderRate,
                sqlStrResolution.apply(primaryType),
                sqlStrResolution.apply(secondaryType),
                isMythical,
                isLegendary
        );
    }
}
