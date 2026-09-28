package com.github.axodenelcorvus.commands;

import com.github.axodenelcorvus.writer.PokemonSqlSeedFilesWriter;
import com.github.axodenelcorvus.extractor.PokeApiDataExtractor;

public class PokemonDataSeedsGenerator {
    private final PokemonSqlSeedFilesWriter pokemonSqlSeedFilesWriter;
    private final PokeApiDataExtractor pokemonDataExtractor;


    public PokemonDataSeedsGenerator(
            PokemonSqlSeedFilesWriter pokemonSqlSeedFilesWriter,
            PokeApiDataExtractor pokeApiDataExtractor
    ) {
        this.pokemonSqlSeedFilesWriter = pokemonSqlSeedFilesWriter;
        pokemonDataExtractor = pokeApiDataExtractor;
    }

    public void extractFiles() {

    }

    public void generateFiles() {

    }

}
