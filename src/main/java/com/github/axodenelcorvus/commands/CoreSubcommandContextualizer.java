package com.github.axodenelcorvus.commands;

import com.github.axodenelcorvus.commands.generator.SeedsGeneratorCommandContext;

import java.util.Optional;

public interface CoreSubcommandContextualizer {

    static Optional<CoreSubcommandContextualizer> supplyContext(String subcommandCandidate) {

        if (subcommandCandidate.equals("generate")) {
            return Optional.of(new SeedsGeneratorCommandContext());
        }

        if (subcommandCandidate.equals("help")) {
            //Add body later
        }

        if (subcommandCandidate.equals("wipe")) {
            //Add body later
        }


        return Optional.empty();
    }

    CoreSubcommandAction setup(String... args);

    HelpDisplayable getHelpDisplayable();

}
