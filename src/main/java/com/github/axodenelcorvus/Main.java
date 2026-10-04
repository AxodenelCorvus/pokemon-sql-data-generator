package com.github.axodenelcorvus;

import com.github.axodenelcorvus.commands.CoreSubcommandAction;
import com.github.axodenelcorvus.commands.CoreSubcommandContextualizer;
import com.github.axodenelcorvus.commands.exceptions.MissingArgumentException;

import java.util.Arrays;

public class Main {

    private static final int ARGUMENT_LENGTH_CAP = 50;

    //Arguments will be case-sensitive
    public static void main(String[] args) {
        try {
            if (args.length == 0)
                throw new MissingArgumentException("An expected subcommand was not given");

            String subcommandGiven = args[0];

            for (String argument : args) {
                if (argument.length() > ARGUMENT_LENGTH_CAP)
                    throw new IllegalArgumentException("A given input passed was too long to process");
            }

            if (args.length == 1) {
                CoreSubcommandAction action = findCoreSubcommandAction(subcommandGiven);
                action.activate();
            }
            else {
                String[] subcommandArguments = Arrays.copyOfRange(args, 1, args.length);
                CoreSubcommandAction action = findCoreSubcommandAction(subcommandGiven, subcommandArguments);
                action.activate();
            }

        }
        catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            //TODO display aggregate help msg
            System.exit(-1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static CoreSubcommandAction findCoreSubcommandAction(String subcommandCandidate, String... withArgs) {
        CoreSubcommandContextualizer subcommandContext = CoreSubcommandContextualizer
                                            .supplyContext(subcommandCandidate)
                                            .orElseThrow(() -> new IllegalArgumentException("Unsupported subcommand provided"));

        return subcommandContext.setup(withArgs);
    }
}