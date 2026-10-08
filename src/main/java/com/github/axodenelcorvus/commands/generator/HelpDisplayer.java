package com.github.axodenelcorvus.commands.generator;

import com.github.axodenelcorvus.commands.HelpDisplayable;
import com.github.axodenelcorvus.writer.PokemonSqlSeedFilesWriter;

public class HelpDisplayer implements HelpDisplayable {

    HelpDisplayer() { }

    @Override
    public String getUsageExample() {
        return "generate <generation> <options>";
    }

    @Override
    public String getCommandDescription() {
        return """
           Generates the SQL files for inserting given Pokemon generation's significant sprites table entries
           and Pokemon table entries.""";
    }

    @Override
    public String[] getRequiredArguments() {
        String[] requiredArgs = new String[2];
        requiredArgs[0] = "<generation>";
        requiredArgs[1] = "\tA single generation 1-9 specified to generate files for. Default directory is %s"
                            .formatted(PokemonSqlSeedFilesWriter.DEFAULT_DIRECTORY_DEST);
        return requiredArgs;
    }

    @Override
    public String[] getOptionsList() {
        String[] options = new String[2];
        options[0] = "--to-dir <destination-directory>";
        options[1] = "\tOverrides default directory to create, if it doesn't exist yet, and to generate files for.\n" +
                    "Must specify relative path that is either current working directory or one that is a level down from it.";
        return options;
    }

}
