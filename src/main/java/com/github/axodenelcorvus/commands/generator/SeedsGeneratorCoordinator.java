package com.github.axodenelcorvus.commands.generator;

import com.github.axodenelcorvus.fetcher.FetchDataThenBundleEntriesTask;
import com.github.axodenelcorvus.fetcher.PokeApiFetcher;
import com.github.axodenelcorvus.model.entry.PokemonEntriesBundle;
import com.github.axodenelcorvus.writer.PokemonSqlSeedFilesWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class SeedsGeneratorCoordinator {
    private final PokemonSqlSeedFilesWriter sqlFilesWriter;
    private final PokeApiFetcher apiFetcher;
    private final Map<Integer, String> singleAltFormDescriptorsByPokemonId;
    public final int BATCH_CAPACITY = 40;

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

    public PokemonEntriesBundle fetchEntries(List<String> slugNames) throws IOException, InterruptedException, ExecutionException {

        List<PokemonEntriesBundle> pokemonEntriesBundleList =
                                    new ArrayList<>(slugNames.size());
        List<Future<PokemonEntriesBundle>> bundledFutures =
                                    new ArrayList<>(BATCH_CAPACITY);

        ExecutorService executorService = Executors.newFixedThreadPool(12);

        try {
            for (String pokemonSlugName : slugNames) {
                FetchDataThenBundleEntriesTask currFetchTaskForPokemon =
                        new FetchDataThenBundleEntriesTask(apiFetcher, pokemonSlugName, singleAltFormDescriptorsByPokemonId);

                bundledFutures.add(executorService.submit(currFetchTaskForPokemon));

                if (bundledFutures.size() == BATCH_CAPACITY)
                    unloadCurrentBatch(bundledFutures, pokemonEntriesBundleList);
            }

            unloadCurrentBatch(bundledFutures, pokemonEntriesBundleList);

        } catch (ExecutionException e) {
            switch (e.getCause()) {
                case Error err -> throw err;
                case IOException ioe -> throw ioe;
                case RuntimeException ex -> throw ex;
                case null, default -> throw e;
            }
        } finally {
            executorService.shutdownNow();
        }

        return PokemonEntriesBundle.flattenBundles(pokemonEntriesBundleList);
    }

    private void unloadCurrentBatch(
            List<Future<PokemonEntriesBundle>> fromFutures,
            List<PokemonEntriesBundle> toBundles) throws InterruptedException, ExecutionException
    {
        while (!fromFutures.isEmpty()) {
            PokemonEntriesBundle currPokemonEntriesBundled = fromFutures.removeLast().get();
            toBundles.add(currPokemonEntriesBundled);
        }
    }

    public void generateFilesFrom(PokemonEntriesBundle entriesBundled) throws IOException {
        sqlFilesWriter.createDestinationDirectoryIfNotExists();
        sqlFilesWriter.writePokemonRowEntries(entriesBundled.pokemonEntries());
        sqlFilesWriter.writeSpriteRowEntries(entriesBundled.pokemonSpriteEntries());
    }

}
