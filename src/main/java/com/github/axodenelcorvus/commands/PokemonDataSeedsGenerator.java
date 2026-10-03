package com.github.axodenelcorvus.commands;

import com.github.axodenelcorvus.extractor.PokeApiFetcher;
import com.github.axodenelcorvus.writer.PokemonSqlSeedFilesWriter;

import java.net.http.HttpClient;

public class PokemonDataSeedsGenerator {
    private final PokemonSqlSeedFilesWriter sqlFilesWriter;
    private final PokeApiFetcher apiFetcher;
    private final HttpClient restClient;

    public PokemonDataSeedsGenerator(PokemonSqlSeedFilesWriter pokemonSqlSeedFilesWriter,
                                     HttpClient client,
                                     PokeApiFetcher pokeApifetcher) {
        sqlFilesWriter = pokemonSqlSeedFilesWriter;
        restClient = client;
        apiFetcher = pokeApifetcher;
    }


    //TODO
    public void extractEntries() {
        var client = HttpClient.newHttpClient();
        try (client) {


        }
    }

    //TODO
    public void generateFiles() {

    }

}
