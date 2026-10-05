package com.github.axodenelcorvus.commands.coordinators;

import com.github.axodenelcorvus.fetcher.PokeApiFetcher;
import com.github.axodenelcorvus.model.entry.PokemonEntriesBundle;
import com.github.axodenelcorvus.writer.PokemonSqlSeedFilesWriter;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.List;

public class SeedsGeneratorCoordinator {
    private final PokemonSqlSeedFilesWriter sqlFilesWriter;
    private final PokeApiFetcher apiFetcher;
    private final HttpClient restClient;

    public SeedsGeneratorCoordinator(PokemonSqlSeedFilesWriter pokemonSqlSeedFilesWriter,
                                     HttpClient client,
                                     PokeApiFetcher pokeApifetcher) {
        sqlFilesWriter = pokemonSqlSeedFilesWriter;
        restClient = client;
        apiFetcher = pokeApifetcher;
    }


    //TODO
    public PokemonEntriesBundle fetchEntries() throws IOException, InterruptedException {
        try (restClient) {
            List<String> slugNames = apiFetcher.fetchPokemonSpeciesSlugNames();

        }

        //TODO
        return null;
    }


    //TODO
    public void generateFilesFrom(PokemonEntriesBundle bundle) throws IOException {
        sqlFilesWriter.createDestinationDirectoryIfNotExists();
        sqlFilesWriter.writePokemonRowEntries(bundle.pokemonEntries());
        sqlFilesWriter.writeSpriteRowEntries(bundle.pokemonSpriteEntries());
    }

}
