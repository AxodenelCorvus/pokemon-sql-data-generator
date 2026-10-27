package com.github.axodenelcorvus.commands.generator;

import com.github.axodenelcorvus.GameGenerationScope;
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
           Generates the SQL files for inserting records on Pokemon from given generation. Includes two files,
           one for significant sprites table entries and another for Pokemon table entries.""";
    }

    @Override
    public String[] getRequiredArguments() {
        String[] requiredArgs = new String[2];
        requiredArgs[0] = " <generation>";
        requiredArgs[1] = "\tA single generation %d-%d specified to generate files for in specified or default directory."
                .formatted(GameGenerationScope.GEN_1.asInt(), GameGenerationScope.GEN_9.asInt());

        return requiredArgs;
    }

    @Override
    public String[] getOptionsList() {
        String[] options = new String[2];
        options[0] = " --to-dir <destination-directory>";
        options[1] = """
                \tOverrides default directory %s to create, if it doesn't exist yet, and generate files for. Must specify
                relative path that is either current working directory or one that is a level down from it.
                """.formatted(PokemonSqlSeedFilesWriter.getDefaultDirDestination());

        return options;
    }

}
