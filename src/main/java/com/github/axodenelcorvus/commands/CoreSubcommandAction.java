package com.github.axodenelcorvus.commands;

import java.util.Optional;

public interface CoreSubcommandAction {

    static Optional<CoreSubcommandAction> supplyCoreSubcommand(String subcommandCandidate) {
        if (subcommandCandidate.equals("generation")) {
            return Optional.of(new GeneratorCommandAction());
        }

        if (subcommandCandidate.equals("wipe")) {
            //Add body later
        }

        if (subcommandCandidate.equals("help")) {
            //Add body later
        }

        return Optional.empty();
    }

    static void runCoreSubcommandAction(CoreSubcommandAction action, String... args) {
        action.setup(args);
        action.execute();
    }

    void setup(String... args);

    boolean isSetup();

    boolean hasBeenExhausted();

    int execute();

}
