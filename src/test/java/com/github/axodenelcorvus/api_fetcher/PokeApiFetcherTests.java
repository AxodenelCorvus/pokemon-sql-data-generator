package com.github.axodenelcorvus.api_fetcher;

import com.github.axodenelcorvus.extractor.PokeApiFetcher;
import com.github.axodenelcorvus.model.dto.util.PokeApiJsonParser;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@Tag("network")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PokeApiFetcherTests {
    private static final int GENERATION_TEST_ID = 1;
    private final PokeApiFetcher fetcher;
    private final HttpClient clientUsed = HttpClient.newHttpClient();

    PokeApiFetcherTests() {
        this.fetcher = new PokeApiFetcher(
                clientUsed,
                new PokeApiJsonParser(),
                GENERATION_TEST_ID
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
