package com.github.axodenelcorvus.commands.wiper;

import com.github.axodenelcorvus.SupportedGameGenerationRange;
import com.github.axodenelcorvus.commands.HelpDisplayable;

public class HelpDisplayer implements HelpDisplayable {

    HelpDisplayer() { }

    @Override
    public String getUsageExample() {
        return "wipe <directory> <targeted-generation-range>";
    }

    @Override
    public String getCommandDescription() {
        return "";
    }

    @Override
    public String[] getRequiredArguments() {
        String[] requiredArgs = new String[4];
        requiredArgs[0] = "<directory>";
        requiredArgs[1] = """
                \tThe directory path for generated SQL files to be removed from, use :DEFUALT: to target default directory made by generator.
                Only relative path to current working directory or a child directory one down from it is accepted. Any nested directories are skipped over in search for files to
                remove.
                """;

        requiredArgs[2] = "<targeted-generation-range>";
        requiredArgs[3] =
                """
                \tTwo generations given, of format <start>-<end> with no spaces before or after bridging dash, that identify generated SQL files to remove.
                To remove files of a single generation, the dashed form or a single integer will be supported. Supported valid generations are %d to %d.
                """.formatted(SupportedGameGenerationRange.FIRST_GEN, SupportedGameGenerationRange.LATEST_GEN);

        return requiredArgs;
    }

    @Override
    public String[] getOptionsList() { return new String[0]; }
}
