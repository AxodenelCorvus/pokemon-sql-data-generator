package com.github.axodenelcorvus.commands.generator;

import com.github.axodenelcorvus.fetcher.PokeApiFetcher;
import com.github.axodenelcorvus.model.entry.PokemonEntriesBundle;
import com.github.axodenelcorvus.writer.PokemonSqlSeedFilesWriter;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.List;

public class SeedsGeneratorCoordinator {
    private final PokemonSqlSeedFilesWriter sqlFilesWriter;
    private final PokeApiFetcher apiFetcher;

    SeedsGeneratorCoordinator(PokemonSqlSeedFilesWriter pokemonSqlSeedFilesWriter,
                              PokeApiFetcher pokeApifetcher) {
        sqlFilesWriter = pokemonSqlSeedFilesWriter;
        apiFetcher = pokeApifetcher;
    }

    public List<String> fetchPokemonSpeciesSlugNames() throws IOException, InterruptedException {
        return apiFetcher.fetchPokemonSpeciesSlugNames();
    }


    //TODO
    public PokemonEntriesBundle fetchEntries(List<String> slugNames) throws IOException, InterruptedException {

        //TODO
        return null;
    }

    public void generateFilesFrom(PokemonEntriesBundle bundle) throws IOException {
        sqlFilesWriter.createDestinationDirectoryIfNotExists();
        sqlFilesWriter.writePokemonRowEntries(bundle.pokemonEntries());
        sqlFilesWriter.writeSpriteRowEntries(bundle.pokemonSpriteEntries());
    }

}
