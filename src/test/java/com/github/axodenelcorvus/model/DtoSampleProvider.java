package com.github.axodenelcorvus.model;

import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;
import com.github.axodenelcorvus.model.dto.PokemonVarietyDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DtoSampleProvider {

    /*
    The keys of each map will be the Pokemon's ID (from Poke API), but not dex ID.
    For example, 10104 is ID of Pokemon belonging to Ninetales "species" with dex ID of 38.

    Using the Pokemon ID we can return both the Pokemon Species DTO, which contains more general info across different Pokemon of same form,
    and the more specific Pokemon DTO that contains more detailed specific information not across the species, mainly their type and region type (from slugName).

    The Pokemon ID and Dex ID only overlap for the base Pokemon representation, which occurs for most Pokemon that will be entered. Those ID's that
    don't overlap are made distinct by being in vastly distant ranges that don't risk overlapping

    Both Map's should share same keys and to help prevent confusion only one Pokemon from each species will be in sample sets.
     */
    private final Map<Integer, PokemonDTO> pokemonDtoById = new HashMap<>();
    private final Map<Integer, PokemonSpeciesDTO>  pokemonSpeciesDtoById = new HashMap<>();

    //Since the actual character values of female sprite link do not matter, will use empty string to save some memory and writing
    private final String SUBBED_FEMALE_URL = "";

    //The variety list for tests that use DtoSampleProvider should not need to use this list, so it is empty to save memory
    private final List<PokemonVarietyDTO> DUMMY_VARIETY_LIST = List.of();

    public DtoSampleProvider() {
        this.addGenerationOneDtos();
        this.addGenerationTwoDtos();
    }

    private void addGenerationOneDtos() {
        pokemonDtoById
                .put(37, new PokemonDTO(37, "vulpix", "fire", null, null));
        pokemonDtoById
                .put(10104, new PokemonDTO(10104, "ninetales-alola", "ice", "fairy", null));
        pokemonDtoById
                .put(80, new PokemonDTO(80, "slowbro","water", "psychic", null));
        pokemonDtoById
                .put(146, new PokemonDTO(146, "moltres", "fire", null, null));
        pokemonDtoById
                .put(10166, new PokemonDTO(10166, "farfetchd-galar", "fighting", null, null));
        pokemonDtoById
                .put(151, new PokemonDTO(151, "mew", "psychic", null, null));
        pokemonDtoById
                .put(123, new PokemonDTO(123, "scyther", "bug", "flying", SUBBED_FEMALE_URL));


        pokemonSpeciesDtoById
                .put(37, new PokemonSpeciesDTO(37, false, 6, "Vulpix", false, false, DUMMY_VARIETY_LIST));
        pokemonSpeciesDtoById
                .put(10104, new PokemonSpeciesDTO(38, false, 6, "Ninetales", false, false, DUMMY_VARIETY_LIST));
        pokemonSpeciesDtoById
                .put(80, new PokemonSpeciesDTO(80, false, 4, "Slowbro", false, false, DUMMY_VARIETY_LIST));
        pokemonSpeciesDtoById
                .put(146, new PokemonSpeciesDTO(146, false, -1, "Moltres", true, false, DUMMY_VARIETY_LIST));
        pokemonSpeciesDtoById
                .put(10166, new PokemonSpeciesDTO(83, false, 4, "Farfetch'd", false, false, DUMMY_VARIETY_LIST));
        pokemonSpeciesDtoById
                .put(151, new PokemonSpeciesDTO(151, false, -1, "Mew", false, true, DUMMY_VARIETY_LIST));
        pokemonSpeciesDtoById
                .put(123, new PokemonSpeciesDTO(123, true, 4, "Scyther", false, false, DUMMY_VARIETY_LIST));
    }

    private void addGenerationTwoDtos() {
        pokemonDtoById
                .put(152, new PokemonDTO(152, "chikorita", "grass", null, null));
        pokemonDtoById
                .put(10235, new PokemonDTO(10235, "sneasel-hisui", "fighting", "poison", SUBBED_FEMALE_URL));
        pokemonDtoById
                .put(214, new PokemonDTO(214, "heracross", "bug", "fighting", SUBBED_FEMALE_URL));
        pokemonDtoById
                .put(243, new PokemonDTO(243, "raikou", "electric", null, null));

        pokemonSpeciesDtoById
                .put(152, new PokemonSpeciesDTO(152, false, 1, "Chikorita", false, false, DUMMY_VARIETY_LIST));
        pokemonSpeciesDtoById
                .put(10235, new PokemonSpeciesDTO(215, true, 4, "Sneasel", false, false, DUMMY_VARIETY_LIST));
        pokemonSpeciesDtoById
                .put(214, new PokemonSpeciesDTO(214, true, 4, "Heracross", false, false, DUMMY_VARIETY_LIST));
        pokemonSpeciesDtoById
                .put(243, new PokemonSpeciesDTO(243, false, -1, "Raikou", true, false, DUMMY_VARIETY_LIST));
    }

    public PokemonDTO getPokemonDTO(int pokemonIdKey) {
        return pokemonDtoById.get(pokemonIdKey);
    }

    public PokemonSpeciesDTO getPokemonSpeciesDTO(int pokemonIdKey) {
        return pokemonSpeciesDtoById.get(pokemonIdKey);
    }

}