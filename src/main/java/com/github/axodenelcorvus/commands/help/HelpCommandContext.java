package com.github.axodenelcorvus.commands.help;

import com.github.axodenelcorvus.commands.CoreSubcommandContextualizer;
import com.github.axodenelcorvus.commands.HelpDisplayable;

public class HelpCommandContext implements CoreSubcommandContextualizer, HelpDisplayable {

    public HelpCommandContext() { }

    @Override
    public HelpCommandAction setup(String... args) {
        if (args.length > 0)
            throw new IllegalArgumentException("Unexpected options or arguments provided when none taken for this command");

        return new HelpCommandAction();
    }

    @Override
    public String getUsageExample() {
        return "help";
    }

    @Override
    public String getCommandDescription() {
        return "\t Displays this message and provides help for all subcommands possible below";
    }

    @Override
    public String[] getOptionsList() {
        return new String[0];
    }

    @Override
    public String[] getRequiredArguments() {
        return new String[0];
    }

    @Override
    public HelpDisplayable getHelpDisplayable() {
        return this;
    }

}
