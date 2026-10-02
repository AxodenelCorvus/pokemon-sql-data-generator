package com.github.axodenelcorvus.api_fetcher;

import com.github.axodenelcorvus.extractor.PokeApiDataFetcher;
import org.junit.jupiter.api.*;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@Tag("network")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PokeApiFetcherTests {
    private final PokeApiDataFetcher fetcher;
    private static final String GENERATION_TEST_URL_USED = "https://pokeapi.co/api/v2/generation/1";
    private final HttpClient clientUsed = HttpClient.newHttpClient();

    PokeApiFetcherTests() {
        this.fetcher = new PokeApiDataFetcher(
                clientUsed,
                new ObjectMapper(),
                URI.create(GENERATION_TEST_URL_USED)
        );
    }

    @AfterAll
    void close() {
        clientUsed.close();
    }

    @Test
    @DisplayName("Slug list for generation 1 returns expected slug name samples and exactly 151 names")
    void testSlugNamesFetched() throws IOException, InterruptedException {
        List<String> expectedSlugSample =
                new ArrayList<>(List.of("bulbasaur", "charmander", "caterpie", "dragonite", "mew", "mewtwo", "hypno", "jynx"));

        int expectedSize = 151;

        List<String> actualSpeciesSlugNames = fetcher.fetchPokemonSpeciesSlugNames();

        assertEquals(expectedSize, actualSpeciesSlugNames.size(), "Fetched slugs list is missing entries");

        assertTrue(expectedSlugSample.removeAll(actualSpeciesSlugNames),
                                        "None of the expected samples were found in fetched list");

        Supplier<String> showSamplesNotFoundInFetchedListMsg = () -> "Expected all samples found in actual slug list fetched, but only found: \n" +
                                                        String.join("\n", expectedSlugSample);

        assertTrue(expectedSlugSample.isEmpty(), showSamplesNotFoundInFetchedListMsg);
    }

}
