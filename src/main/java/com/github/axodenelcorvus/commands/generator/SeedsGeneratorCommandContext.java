package com.github.axodenelcorvus.commands.generator;

import com.github.axodenelcorvus.SupportedGameGenerationRange;
import com.github.axodenelcorvus.commands.CoreSubcommandAction;
import com.github.axodenelcorvus.commands.CoreSubcommandContextualizer;
import com.github.axodenelcorvus.commands.exceptions.MissingArgumentException;
import com.github.axodenelcorvus.commands.exceptions.RepeatedOptionException;
import com.github.axodenelcorvus.commands.exceptions.UnsupportedOptionException;
import com.github.axodenelcorvus.fetcher.PokeApiFetcher;
import com.github.axodenelcorvus.fetcher.SingleAlternativeFormMapProvider;
import com.github.axodenelcorvus.model.dto.parser.PokeApiJsonParser;
import com.github.axodenelcorvus.model.entry.FossilPokemonChecker;
import com.github.axodenelcorvus.writer.PokemonSqlSeedFilesWriter;

import java.net.http.HttpClient;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SeedsGeneratorCommandContext implements CoreSubcommandContextualizer {

    private Path optionalDirectoryDestination;
    private int generationScope;

    public SeedsGeneratorCommandContext() {}

    /*
    Rules for options:
    - All are long flags, start with  "--" and at least 4 characters (more than one character for body of flag)
    - No repeated flags allowed
     */
    private void configureWithOptions(String... options) {
        Set<String> encounteredFlags = new HashSet<>();

        //Note first argument string of options segment is supposed to be an option,
        //any arguments should be process and passed over by assisting private method (updating i)
        for (int i = 0; i < options.length; i++) {
            String optionCandidate = options[i];

            if (!optionCandidate.startsWith("--") ||  optionCandidate.length() < 5)
                throw new IllegalArgumentException("%d Invalid option format provided for %s or unexpected option argument provided".formatted(i,optionCandidate));

            optionCandidate = optionCandidate.substring(2);

            boolean isFlagEncounteredAgain = !encounteredFlags.add(optionCandidate);

            if (isFlagEncounteredAgain)
                throw new RepeatedOptionException("Command given does not accept repeated options %s".formatted(optionCandidate));

            boolean moreOptionsToProcess = options.length - (i + 1) != 0;

            if (moreOptionsToProcess)
                i = configureOptionAtIndex(i, optionCandidate, Arrays.copyOfRange(options, i + 1, options.length));
            else
                configureOptionAtIndex(i, optionCandidate);
        }
    }

    //Can move index forward when a flag consumes arguments(s) and is given argument(s)
    private int configureOptionAtIndex(int currIndex, String optionCandidate, String... remainingOptionsAhead) {
        if (optionCandidate.equals("to-dir")) {
            if (remainingOptionsAhead.length == 0)
                throw new MissingArgumentException("Required destination directory for files with to-dir, but none were provided");

            this.setOptionalDirectoryDestination(remainingOptionsAhead[0]);
            currIndex++;
        }
        else {
            throw new UnsupportedOptionException("Unsupported option %s ".formatted(optionCandidate));
        }

        return currIndex;
    }

    private void setOptionalDirectoryDestination(String destinationPath) {
        optionalDirectoryDestination = Path.of(destinationPath);
    }

    private void setGenerationScope(String generationFromUser) {
        int intGeneration;

        try { intGeneration = Integer.parseInt(generationFromUser); }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Generation given could not be understood | %s"
                    .formatted(SupportedGameGenerationRange.getOutOfRangeErrMsg()));
        }


        if (!SupportedGameGenerationRange.isValidGeneration(intGeneration))
            throw new IllegalArgumentException(SupportedGameGenerationRange.getOutOfRangeErrMsg());

        this.generationScope = intGeneration;
    }

    public Path getOptionalDirectoryDestination() { return optionalDirectoryDestination; }

    public int getGenerationScope() { return generationScope; }


    @Override
    public CoreSubcommandAction setup(String... args) {

        if (args.length == 0)
            throw new MissingArgumentException("Required generation value, but none received");

        this.setGenerationScope(args[0]);

        if (args.length >= 2)
            this.configureWithOptions(Arrays.copyOfRange(args, 1, args.length));


        //WIRING LOGIC
        HttpClient client = HttpClient.newBuilder()
                                .connectTimeout(Duration.ofSeconds(10))
                                .build();

        var pokemonFossilChecker = new FossilPokemonChecker(generationScope);

        var sqlSeedsFileWriter =
                new PokemonSqlSeedFilesWriter(generationScope, optionalDirectoryDestination);

        var pokeApiDataFetcher =
                new PokeApiFetcher(client, new PokeApiJsonParser(), generationScope);

        Map<Integer, String> singleAltFormDescriptorsByPokemonId =
                SingleAlternativeFormMapProvider.from(generationScope);

        SeedsGeneratorCoordinator seedsGeneratorCoordinator = new SeedsGeneratorCoordinator(
                sqlSeedsFileWriter,
                pokeApiDataFetcher,
                singleAltFormDescriptorsByPokemonId,
                pokemonFossilChecker
        );

        return new SeedsGeneratorCommandAction(seedsGeneratorCoordinator, client);
    }

    @Override
    public HelpDisplayer getHelpDisplayable() {
        return new HelpDisplayer();
    }
}