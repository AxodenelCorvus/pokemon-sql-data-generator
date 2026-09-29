package com.github.axodenelcorvus.commands;

import com.github.axodenelcorvus.commands.exceptions.MissingArgumentException;
import com.github.axodenelcorvus.commands.exceptions.RepeatedOptionException;
import com.github.axodenelcorvus.commands.exceptions.UnsupportedOptionException;
import com.github.axodenelcorvus.writer.PokemonSqlSeedFilesWriter;
import com.github.axodenelcorvus.extractor.PokeApiDataExtractor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GeneratorCommandAction implements CoreSubcommandAction {
    public static final boolean REPEAT_FLAGS = false;

    //Supported range of generations
    public static final int FIRST_GENERATION = 1;
    public static final int LATEST_GENERATION = 9;

    private String optionalDirectoryDestination;
    private int generation;
    private PokemonDataSeedsGenerator pokemonDataSeedsGenerator;
    private boolean hasBeenExhausted;

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
        optionalDirectoryDestination = destinationPath;
    }

    private void setGeneration(String generationFromUser) {
        int intGeneration;

        try { intGeneration = Integer.parseInt(generationFromUser); }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Generation could not be understood |" +
                    " Specified generation not in supported range %d to %d".formatted(FIRST_GENERATION, LATEST_GENERATION));
        }

        boolean isGenerationInvalid = intGeneration < FIRST_GENERATION || intGeneration > LATEST_GENERATION;

        if (isGenerationInvalid)
            throw new IllegalArgumentException("Specified generation not in supported range %d to %d".formatted(FIRST_GENERATION, LATEST_GENERATION));

        this.generation = intGeneration;
    }

    public String getOptionalDirectoryDestination() { return optionalDirectoryDestination; }

    public int getGeneration() { return generation; }

    @Override
    public boolean isSetup() {
        return pokemonDataSeedsGenerator != null;
    }

    @Override
    public boolean hasBeenExhausted() {
        return hasBeenExhausted;
    }

    @Override
    public void setup(String... args) {
        //Do not run on multiple calls, silently ignore
        if (this.isSetup())
            return;

        if (args.length == 0)
            throw new MissingArgumentException("Required generation in range %d to %d".formatted(FIRST_GENERATION, LATEST_GENERATION));

        this.setGeneration(args[0]);

        if (args.length >= 2)
            this.configureWithOptions(Arrays.copyOfRange(args, 1, args.length));


        //WIRING LOGIC
        this.pokemonDataSeedsGenerator = new PokemonDataSeedsGenerator(
                new PokemonSqlSeedFilesWriter(generation, optionalDirectoryDestination),
                new PokeApiDataExtractor()
        );
    }

    @Override
    public int execute() {
        if (!this.isSetup())
            throw new IllegalStateException("Command's environment has not been set up");
        if (this.hasBeenExhausted())
            throw new IllegalStateException("Command has been exhausted");

        //Call extract

        //Call generate files

        hasBeenExhausted = false;
        return 0;
    }

}