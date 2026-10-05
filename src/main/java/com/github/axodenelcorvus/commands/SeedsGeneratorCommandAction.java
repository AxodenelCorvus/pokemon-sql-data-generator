package com.github.axodenelcorvus.commands;

import com.github.axodenelcorvus.commands.coordinators.SeedsGeneratorCoordinator;
import com.github.axodenelcorvus.model.entry.PokemonEntriesBundle;

import java.io.IOException;

public class SeedsGeneratorCommandAction implements CoreSubcommandAction {
    private final SeedsGeneratorCoordinator pokemonSeedsGeneratorCoordinator;

    SeedsGeneratorCommandAction(SeedsGeneratorCoordinator seedsGeneratorCoordinator) {
        this.pokemonSeedsGeneratorCoordinator = seedsGeneratorCoordinator;
    }

    @Override
    public void activate() {
        try {
            PokemonEntriesBundle bundle;

            bundle = pokemonSeedsGeneratorCoordinator.fetchEntries();

            pokemonSeedsGeneratorCoordinator.generateFilesFrom(bundle);

        } catch (InterruptedException ie) {

        } catch (IOException ioe) {

        }
    }
}