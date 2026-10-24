package com.github.axodenelcorvus.commands.help;

import com.github.axodenelcorvus.commands.CoreSubcommandAction;

import java.util.List;
import java.util.function.Supplier;

public class HelpCommandAction implements CoreSubcommandAction {

    HelpCommandAction() { }

    @Override
    public int activate() {
        List<Supplier<String>> helpDisplayMessages = AggregateHelpMessageProvider.supplyDisplayMessages();

        for (Supplier<String> helpDisplay : helpDisplayMessages) {
            System.out.println(helpDisplay.get());
        }
        
        return 0;
    }

}
