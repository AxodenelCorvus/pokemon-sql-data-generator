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

        for (HelpDisplayable displayable : helpDisplayables) {
            helpMessageSuppliers.add(() -> {
                    return
                    """
                    %s  -  %s
                    
                    %s
                    
                    %s
                    """.formatted(
                            displayable.getUsageExample(),
                            displayable.getCommandDescription(),
                            String.join("\n", displayable.getRequiredArguments()),
                            String.join("\n", displayable.getOptionsList())
                    );
            });
        }

        return helpMessageSuppliers;
    }

}
