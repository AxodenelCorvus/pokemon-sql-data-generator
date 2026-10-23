package com.github.axodenelcorvus.commands;

import com.github.axodenelcorvus.commands.generator.SeedsGeneratorCommandContext;
import com.github.axodenelcorvus.commands.help.HelpCommandContext;

import java.util.Optional;

public interface CoreSubcommandContextualizer {

    static Optional<CoreSubcommandContextualizer> supplyContext(String subcommandCandidate) {
        return switch(subcommandCandidate) {
            case ("generate")    -> Optional.of(new SeedsGeneratorCommandContext());
            case ("help")        -> Optional.of(new HelpCommandContext());
            default -> Optional.empty();
        };
    }

    CoreSubcommandAction setup(String... args);

    HelpDisplayable getHelpDisplayable();

}
