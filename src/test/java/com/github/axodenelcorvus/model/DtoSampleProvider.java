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

    public DtoSampleProvider() {
        List<PokemonVarietyDTO> emptyList = List.of();

        pokemonDtoById.put(37, new PokemonDTO(37, "vulpix", "fire", null));
        pokemonDtoById.put(10104, new PokemonDTO(10104, "ninetales-alola", "ice", "fairy"));
        pokemonDtoById.put(80, new PokemonDTO(80, "slowbro","water", "psychic"));
        pokemonDtoById.put(146, new PokemonDTO(146, "moltres", "fire", null));
        pokemonDtoById.put(10166, new PokemonDTO(10166, "farfetchd-galar", "fighting", null));
        pokemonDtoById.put(151, new PokemonDTO(151, "mew", "psychic", null));
        pokemonDtoById.put(10235, new PokemonDTO(10235, "sneasel-hisui", "fighting", "poison"));

        pokemonSpeciesDtoById
                .put(37, new PokemonSpeciesDTO(37, false, 6, "Vulpix", false, false, emptyList));
        pokemonSpeciesDtoById
                .put(10104, new PokemonSpeciesDTO(38, false, 6, "Ninetales", false, false, emptyList));
        pokemonSpeciesDtoById
                .put(80, new PokemonSpeciesDTO(80, false, 4, "Slowbro", false, false, emptyList));
        pokemonSpeciesDtoById
                .put(146, new PokemonSpeciesDTO(146, false, -1, "Moltres", true, false, emptyList));
        pokemonSpeciesDtoById
                .put(10166, new PokemonSpeciesDTO(83, false, 4, "Farfetch'd", false, false, emptyList));
        pokemonSpeciesDtoById
                .put(151, new PokemonSpeciesDTO(151, false, -1, "Mew", false, true, emptyList));
        pokemonSpeciesDtoById
                .put(10235, new PokemonSpeciesDTO(10235, true, 4, "Sneasel", false, false, emptyList));
    }


    public PokemonDTO getPokemonDTO(int pokemonIdKey) {
        return pokemonDtoById.get(pokemonIdKey);
    }

    public PokemonSpeciesDTO getPokemonSpeciesDTO(int pokemonIdKey) {
        return pokemonSpeciesDtoById.get(pokemonIdKey);
    }

}
