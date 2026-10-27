package com.github.axodenelcorvus;

import com.github.axodenelcorvus.commands.CoreSubcommandAction;
import com.github.axodenelcorvus.commands.CoreSubcommandContextualizer;
import com.github.axodenelcorvus.commands.exceptions.MissingArgumentException;
import com.github.axodenelcorvus.commands.help.HelpCommandContext;

import java.util.Arrays;

public class Main {

    private static final int ARGUMENT_LENGTH_CAP = 50;

    private static final int BAD_INPUT_STATUS_CODE = 2;

    //Arguments will be case-sensitive
    public static void main(String[] args) {
        //Assume generic error status for exit until actual status obtained
        int actionStatusResult = 1;

        try {
            if (args.length == 0)
                throw new MissingArgumentException("An expected subcommand was not given");

            for (String argument : args) {
                if (argument.length() > ARGUMENT_LENGTH_CAP)
                    throw new IllegalArgumentException("A given input passed was too long to process");
            }

            String subcommandGiven = args[0];

            if (subcommandGiven.startsWith("-"))
                throw new IllegalArgumentException("Expected subcommand but was given unexpected option");

            CoreSubcommandAction cmdAction;

            if (args.length == 1) {
                cmdAction = findCoreSubcommandAction(subcommandGiven);
            } else {
                String[] subcommandArguments = Arrays.copyOfRange(args, 1, args.length);
                cmdAction = findCoreSubcommandAction(subcommandGiven, subcommandArguments);
            }

            actionStatusResult = cmdAction.activate();
        } catch (IllegalArgumentException e) {
            System.err.printf("%n%s%n%n", e.getMessage());
            HelpCommandContext helpCommandContext = new HelpCommandContext();
            helpCommandContext.setup().activate();
            actionStatusResult = BAD_INPUT_STATUS_CODE;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        System.exit(actionStatusResult);
    }

    private static CoreSubcommandAction findCoreSubcommandAction(String subcommandCandidate, String... withArgs) {
        CoreSubcommandContextualizer subcommandContext = CoreSubcommandContextualizer
                .supplyContext(subcommandCandidate)
                .orElseThrow(() -> new IllegalArgumentException("Unsupported subcommand provided"));

        return subcommandContext.setup(withArgs);
    }
}