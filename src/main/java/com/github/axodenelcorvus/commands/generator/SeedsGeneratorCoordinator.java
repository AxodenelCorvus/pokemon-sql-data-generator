package com.github.axodenelcorvus.commands.generator;

import com.github.axodenelcorvus.fetcher.FetchDataThenBundleEntriesTask;
import com.github.axodenelcorvus.fetcher.PokeApiFetcher;
import com.github.axodenelcorvus.model.entry.FossilPokemonChecker;
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
    private final FossilPokemonChecker fossilPokemonChecker;

    SeedsGeneratorCoordinator(PokemonSqlSeedFilesWriter pokemonSqlSeedFilesWriter,
                              PokeApiFetcher pokeApifetcher,
                              Map<Integer, String> singleAltFormDescriptorsByPokemonId,
                              FossilPokemonChecker fossilPokemonChecker
    ) {
        sqlFilesWriter = pokemonSqlSeedFilesWriter;
        apiFetcher = pokeApifetcher;
        this.singleAltFormDescriptorsByPokemonId = singleAltFormDescriptorsByPokemonId;
        this.fossilPokemonChecker = fossilPokemonChecker;
    }

    public List<String> fetchPokemonSpeciesSlugNames() throws IOException, InterruptedException {
        return apiFetcher.fetchPokemonSpeciesSlugNames();
    }

    public PokemonEntriesBundle fetchEntries(List<String> slugNames) throws IOException, InterruptedException, ExecutionException {
        System.out.println("Fetching Data...");

        List<PokemonEntriesBundle> pokemonEntriesBundleList =
                                    new ArrayList<>(slugNames.size());
        List<Future<PokemonEntriesBundle>> bundledFutures =
                                    new ArrayList<>(BATCH_CAPACITY);

        ExecutorService executorService = Executors.newFixedThreadPool(12);

        int countOfFetchedPokemon = 0;
        int totalNumToBeFetched = slugNames.size();

        try {
            for (String pokemonSlugName : slugNames) {
                FetchDataThenBundleEntriesTask currFetchTaskForPokemon = new FetchDataThenBundleEntriesTask(
                        apiFetcher,
                        pokemonSlugName,
                        singleAltFormDescriptorsByPokemonId,
                        fossilPokemonChecker
                );

                bundledFutures.add(executorService.submit(currFetchTaskForPokemon));
                countOfFetchedPokemon++;

                if (bundledFutures.size() == BATCH_CAPACITY || countOfFetchedPokemon == totalNumToBeFetched) {
                    unloadCurrentBatch(bundledFutures, pokemonEntriesBundleList);
                    printOutFetchProgressReport(countOfFetchedPokemon, totalNumToBeFetched);
                }
            }
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

    private void printOutFetchProgressReport(double countOfFetchedPokemon, double totalNumToBeFetched) {
        String progressMsg = "* Entries Fetched and Compiled: %3.0f%% *".formatted(
                (countOfFetchedPokemon / totalNumToBeFetched) * 100
        );
        String borderWrap = "*".repeat(progressMsg.length());

        System.out.println('\t' + borderWrap);
        System.out.println('\t' + progressMsg);
        System.out.println('\t' + borderWrap);
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
        System.out.println("Preparing Directory and Writing New Files...");

        sqlFilesWriter.createDestinationDirectoryIfNotExists()
                .ifPresent((dir) -> System.out.printf("\tCreated Directory: %s%n", dir));

        sqlFilesWriter.writePokemonRowEntries(entriesBundled.pokemonEntries());
        sqlFilesWriter.writeSpriteRowEntries(entriesBundled.pokemonSpriteEntries());

        System.out.printf(
                "\tGenerated File: ./%s/%s %n",
                sqlFilesWriter.getDirectoryDestination(),
                sqlFilesWriter.getPokemonEntriesInsertionPath().getFileName()
        );
        System.out.printf(
                "\tGenerated File: ./%s/%s %n",
                sqlFilesWriter.getDirectoryDestination(),
                sqlFilesWriter.getPokemonEntriesInsertionPath().getFileName()
        );
    }

}