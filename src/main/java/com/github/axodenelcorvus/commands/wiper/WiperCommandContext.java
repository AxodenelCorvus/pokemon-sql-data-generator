package com.github.axodenelcorvus.commands.wiper;

import com.github.axodenelcorvus.commands.CoreSubcommandAction;
import com.github.axodenelcorvus.commands.CoreSubcommandContextualizer;
import com.github.axodenelcorvus.commands.HelpDisplayable;

public class WiperCommandContext implements CoreSubcommandContextualizer {

    @Override
    public CoreSubcommandAction setup(String... args) {
        return null; //Return PokemonSqlSeedFilesRemover
    }

    @Override
    public HelpDisplayable getHelpDisplayable() {
        return new HelpDisplayer();
    }

}
