package com.github.axodenelcorvus;

import com.github.axodenelcorvus.commands.CoreSubcommandAction;
import com.github.axodenelcorvus.commands.exceptions.MissingArgumentException;

import java.util.Arrays;
import java.util.Optional;

public class Main {

    private static final int ARGUMENT_LENGTH_CAP = 50;

    //Arguments will be case-sensitive
    public static void main(String[] args) {
        try {
            if (args.length == 0)
                throw new MissingArgumentException("An expected subcommand was not given");

            String subcommandGiven = args[0];

            for (int i = 0; i < args.length; i++) {
                if (args[i].length() > ARGUMENT_LENGTH_CAP)
                    throw new IllegalArgumentException("A given input passed was too long to process");
            }

            if (args.length == 1) {
                runCoreSubcommandAction(subcommandGiven);
            }
            else {
                String[] subcommandArguments = Arrays.copyOfRange(args, 1, args.length);
                runCoreSubcommandAction(subcommandGiven, subcommandArguments);
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

    private static void runCoreSubcommandAction(String subcommandCandidate, String... withArgs) {
        Optional<CoreSubcommandAction> subCmdActionFound = CoreSubcommandAction
                .supplyCoreSubcommand(subcommandCandidate);

        var subcommandAction = subCmdActionFound.orElseThrow(() ->
                                new IllegalArgumentException("Unsupported subcommand provided"));

        CoreSubcommandAction
                .runCoreSubcommandAction(subcommandAction, withArgs);

    }
}