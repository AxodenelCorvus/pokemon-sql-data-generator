package com.github.axodenelcorvus.commands.generator;

import com.github.axodenelcorvus.fetcher.PokeApiFetcher;
import com.github.axodenelcorvus.model.entry.PokemonEntriesBundle;
import com.github.axodenelcorvus.writer.PokemonSqlSeedFilesWriter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class SeedsGeneratorCoordinator {
    private final PokemonSqlSeedFilesWriter sqlFilesWriter;
    private final PokeApiFetcher apiFetcher;
    private final Map<Integer, String> singleAltFormDescriptorsByPokemonId; //TODO Use in fetchEntries method
    public final int BATCH_CAPACITY = 40; //TODO Use in fetchEntries method, review access lvl

    SeedsGeneratorCoordinator(PokemonSqlSeedFilesWriter pokemonSqlSeedFilesWriter,
                              PokeApiFetcher pokeApifetcher,
                              Map<Integer, String> singleAltFormDescriptorsByPokemonId)
    {
        sqlFilesWriter = pokemonSqlSeedFilesWriter;
        apiFetcher = pokeApifetcher;
        this.singleAltFormDescriptorsByPokemonId = singleAltFormDescriptorsByPokemonId;
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
