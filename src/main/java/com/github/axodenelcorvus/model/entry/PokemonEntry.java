package com.github.axodenelcorvus.model.entry;

import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;

public class PokemonEntry implements SqlTupleSerializable {
    private int id;
    private int nationalDexId;
    private String name;
    private Integer genderRate;
    private String primaryType;
    private String secondaryType;
    private boolean isMythical;
    private boolean isLegendary;

    private PokemonEntry() { }

    public static PokemonEntry from(PokemonDTO pokemonDTO, PokemonSpeciesDTO pokemonSpeciesDTO) {
        PokemonEntry result = new PokemonEntry();

        result.id = pokemonDTO.id();
        result.nationalDexId = pokemonSpeciesDTO.dexId();
        result.name = pokemonSpeciesDTO.name();
        result.primaryType = capitalizePokemonType(pokemonDTO.primaryType());
        result.secondaryType = capitalizePokemonType(pokemonDTO.secondaryType());
        result.isMythical = pokemonSpeciesDTO.isMythical();
        result.isLegendary = pokemonSpeciesDTO.isLegendary();
        result.setGenderRate(pokemonSpeciesDTO.genderRate());

        return result;
    }

    public int getId() {
        return id;
    }

    public int getNationalDexId() {
        return nationalDexId;
    }

    public String getName() {
        return name;
    }

    public Integer getGenderRate() {
        return genderRate;
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public String getSecondaryType() {
        return secondaryType;
    }

    public boolean isMythical() {
        return isMythical;
    }

    public boolean isLegendary() {
        return isLegendary;
    }

    private void setGenderRate(Integer genderRate) {
        this.genderRate = genderRate < 0 ? null : genderRate;
    }

    private static String capitalizePokemonType(String type) {
        if (type == null)
            return null;
        else
            return Character.toUpperCase(type.charAt(0)) + type.substring(1);
    }

    public static final String TABLE_NAME = "pokemon";
    public static final String COLUMN_LIST = "(id, national_dex_id, name, gender_rate, primary_type, secondary_type, is_mythical, is_legendary)";

    public String toSqlTuple(SqlStringResolver sqlStrResolution) {
        return "(%d, %d, %s, %d, %s, %s, %b, %b)".formatted(
                id,
                nationalDexId,
                sqlStrResolution.apply(name),
                genderRate,
                sqlStrResolution.apply(primaryType),
                sqlStrResolution.apply(secondaryType),
                isMythical,
                isLegendary
        );
    }
}
