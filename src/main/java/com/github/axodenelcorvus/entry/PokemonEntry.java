package com.github.axodenelcorvus.entry;

public record PokemonEntry(int id,
                           int nationalPokeDexId,
                           String name,
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

    public static final String TABLE_NAME = "pokemon";
    public static final String COLUMN_LIST = "(id, national_dex_id, name, gender_rate, primary_type, secondary_type, is_mythical, is_legendary)";

    public String toSqlTuple(SqlStringResolver sqlStrResolution) {
        return  "(%d, %d, %s, %d, %s, %s, %b, %b)".formatted(
                id,
                nationalPokeDexId,
                sqlStrResolution.apply(name),
                genderRate,
                sqlStrResolution.apply(primaryType),
                sqlStrResolution.apply(secondaryType),
                isMythical,
                isLegendary
        );
    }
}
