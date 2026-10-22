package com.github.axodenelcorvus.commands.wiper;

import com.github.axodenelcorvus.commands.CoreSubcommandAction;
import com.github.axodenelcorvus.writer.PokemonSqlSeedFilesRemover;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

class WiperCommandAction implements CoreSubcommandAction {
    private final PokemonSqlSeedFilesRemover sqlSeedFilesRemover;

    public WiperCommandAction(PokemonSqlSeedFilesRemover pokemonSqlSeedFilesRemover) {
        sqlSeedFilesRemover = pokemonSqlSeedFilesRemover;
    }

    @Override
    public void activate() {
        try {
            sqlSeedFilesRemover.removeAllTargetedSqlSeedFiles();
            System.out.println("INFO: Removed number of files: " + sqlSeedFilesRemover.getRemovalCount());

        } catch (NoSuchFileException e) {
            System.err.println("Targeted file was removed right before program's removal attempt or some unexpected error occurred");
            System.err.println("WARNING: Not all files targeted for removal were removed, removal incomplete");
        } catch (IOException ioe) {
            System.err.println("WARNING: Not all files targeted for removal were removed, removal incomplete\n");
            ioe.printStackTrace();
        }
    }
}
