package com.github.axodenelcorvus.commands.help;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.function.Supplier;

class AggregateHelpMessageProviderTest {

    @Test
    void testFoo() {
        List<Supplier<String>> displayMessages = AggregateHelpMessageProvider.supplyDisplayMessages();
        Supplier<String> actualFirstDisplayMsg  = displayMessages.getFirst();
        String expectedDisplayMsg =
                """
                generate <generation> <options>  -  Generates the SQL files for inserting given Pokemon generation's significant sprites table entries
                and Pokemon table entries.
                
                <generation>
                \tA single generation 1-9 specified to generate files for. Default directory is generated_sql_files
                
                --to-dir <destination-directory>
                \tOverrides default directory to create, if it doesn't exist yet, and to generate files for.
                Must specify relative path that is either current working directory or one that is a level down from it.
                """;

        assertEquals(expectedDisplayMsg, actualFirstDisplayMsg.get());
    }

}
