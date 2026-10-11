package com.github.axodenelcorvus.commands.generator;

import com.github.axodenelcorvus.commands.CoreSubcommandAction;
import com.github.axodenelcorvus.model.entry.PokemonEntriesBundle;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;
import java.nio.file.FileSystemException;
import java.util.List;

public class SeedsGeneratorCommandAction implements CoreSubcommandAction {
    private final SeedsGeneratorCoordinator pokemonSeedsGeneratorCoordinator;
    private final HttpClient client;

    SeedsGeneratorCommandAction(SeedsGeneratorCoordinator seedsGeneratorCoordinator, HttpClient client) {
        this.pokemonSeedsGeneratorCoordinator = seedsGeneratorCoordinator;
        this.client = client;
    }

    @Override
    public void activate() {
        try (client) {
            PokemonEntriesBundle generationEntriesBundled;

            List<String> pokemonSpeciesSlugNames = pokemonSeedsGeneratorCoordinator.fetchPokemonSpeciesSlugNames();

            generationEntriesBundled = pokemonSeedsGeneratorCoordinator.fetchEntries(pokemonSpeciesSlugNames);

            pokemonSeedsGeneratorCoordinator.generateFilesFrom(generationEntriesBundled);
        }
        catch (InterruptedException ignored) { }
        /*These exceptions aren't result of bad input, but some external failure or issue (networking issues, API provider down, etc)
        in this case we will handle them by printing message and not showing help for any commands*/
        catch (HttpConnectTimeoutException e) {
            System.out.println("Connection Timeout: A connection with server was not established in enough time");
        } catch (HttpTimeoutException e) {
            System.out.println("Response Timeout: A response could not be fulfilled and timed out");
        } catch (ConnectException | UnknownHostException e) {
            System.out.println("Server could not be reached");
        } catch (FileSystemException e) {
            if (e.getFile() != null)
                System.err.printf("Issue using file %s%n", e.getFile());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}