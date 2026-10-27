package com.github.axodenelcorvus.commands.help;

import com.github.axodenelcorvus.commands.HelpDisplayable;
import com.github.axodenelcorvus.commands.generator.SeedsGeneratorCommandContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

class AggregateHelpMessageProvider {

    private AggregateHelpMessageProvider() { }

    public static List<Supplier<String>> supplyDisplayMessages() {
        List<HelpDisplayable> helpDisplayables = new ArrayList<>();
        List<Supplier<String>> helpMessageSuppliers = new ArrayList<>();

        helpDisplayables.add(new SeedsGeneratorCommandContext().getHelpDisplayable());
        helpDisplayables.add(new HelpCommandContext().getHelpDisplayable());

        StringBuilder helpMsgBuilder = new StringBuilder();

        for (HelpDisplayable helpDisplayable : helpDisplayables) {
            helpMsgBuilder
                    .append(helpDisplayable.getUsageExample())
                    .append("  -  ")
                    .append(helpDisplayable.getCommandDescription())
                    .append("\n\n");

            for (String requiredArgumentHelpDisplay : helpDisplayable.getRequiredArguments()) {
                helpMsgBuilder.append(requiredArgumentHelpDisplay);
                helpMsgBuilder.append("\n");
            }

            if (helpDisplayable.getOptionsList().length != 0) {
                helpMsgBuilder.append("\nOPTIONS\n\n");
                for (String optionHelpDisplay : helpDisplayable.getOptionsList()) {
                    helpMsgBuilder.append(optionHelpDisplay);
                    helpMsgBuilder.append('\n');
                }
            }

            String helpMessage = helpMsgBuilder.toString();

            helpMessageSuppliers.add(() -> helpMessage);
            helpMsgBuilder.setLength(0);
        }

        return helpMessageSuppliers;
    }
}
