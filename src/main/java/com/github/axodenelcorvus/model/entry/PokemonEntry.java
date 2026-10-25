package com.github.axodenelcorvus.model.entry;

import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;

import java.util.Objects;

public final class PokemonEntry implements SqlTupleSerializable {
    private int id;
    private int nationalDexId;
    private String name;
    private Integer genderRate;
    private String primaryType;
    private String secondaryType;
    private boolean isMythical;
    private boolean isLegendary;
    private boolean isFossil;

    private PokemonEntry() { }

    public static PokemonEntry from(PokemonDTO pokemonDTO, PokemonSpeciesDTO pokemonSpeciesDTO, FossilPokemonChecker fossilChecker) {
        PokemonEntry result = new PokemonEntry();

        result.id = pokemonDTO.id();
        result.nationalDexId = pokemonSpeciesDTO.dexId();
        result.name = pokemonSpeciesDTO.name();
        result.primaryType = capitalizePokemonType(pokemonDTO.primaryType());
        result.secondaryType = capitalizePokemonType(pokemonDTO.secondaryType());
        result.isMythical = pokemonSpeciesDTO.isMythical();
        result.isLegendary = pokemonSpeciesDTO.isLegendary();
        result.setGenderRate(pokemonSpeciesDTO.genderRate());
        result.isFossil = fossilChecker.checkIfFossil(pokemonSpeciesDTO.dexId());

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

    public boolean isFossil() {
        return isFossil;
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
    public static final String COLUMN_LIST = "(id, national_dex_id, name, gender_rate, primary_type, secondary_type, is_mythical, is_legendary, is_fossil)";

    @Override
    public String toSqlTuple(SqlStringResolver sqlStrResolution) {
        return "(%d, %d, %s, %d, %s, %s, %b, %b, %b)".formatted(
                id,
                nationalDexId,
                sqlStrResolution.apply(name),
                genderRate,
                sqlStrResolution.apply(primaryType),
                sqlStrResolution.apply(secondaryType),
                isMythical,
                isLegendary,
                isFossil
        );
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PokemonEntry other
                && id == other.id
                && nationalDexId == other.nationalDexId
                && Objects.equals(genderRate, other.genderRate)
                && Objects.equals(primaryType, other.primaryType)
                && Objects.equals(secondaryType, other.secondaryType)
                && isMythical == other.isMythical
                && isLegendary == other.isLegendary
                && isFossil == other.isFossil;
    }

    @Override
    public int hashCode() {
        //use primary key for hash
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        char mythicalToChar = isMythical ? 'T' : 'F';
        char legendaryToChar = isLegendary ? 'T' : 'F';
        char fossilToChar = isFossil ? 'T' : 'F';

        return "PokemonEntry{" +
                "id=" + id +
                ", natDexId=" + nationalDexId +
                ", name=\"" + name + '\"' +
                ", genderRate=" + genderRate +
                ", primaryType=\"" + primaryType + '\"' +
                ", secondaryType=\"" + secondaryType + '\"' +
                ", isMythical=" + mythicalToChar +
                ", isLegendary=" + legendaryToChar +
                ", isFossil=" + fossilToChar +
                '}';
    }
}
