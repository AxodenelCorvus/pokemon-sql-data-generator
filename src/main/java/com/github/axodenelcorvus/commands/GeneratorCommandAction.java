package com.github.axodenelcorvus.commands;

import com.github.axodenelcorvus.model.entry.PokemonEntriesBundle;

import java.io.IOException;

public class GeneratorCommandAction implements CoreSubcommandAction {
    private final PokemonDataSeedsGenerator pokemonDataSeedsGenerator;

    GeneratorCommandAction(PokemonDataSeedsGenerator pokemonDataSeedsGenerator) {
        this.pokemonDataSeedsGenerator = pokemonDataSeedsGenerator;
    }

    @Override
    public void activate() {
        try {
            PokemonEntriesBundle bundle;

            bundle = pokemonDataSeedsGenerator.extractEntries();

            pokemonDataSeedsGenerator.generateFiles(bundle);

        } catch (InterruptedException ie) {

        } catch (IOException ioe) {

        }
    }
}