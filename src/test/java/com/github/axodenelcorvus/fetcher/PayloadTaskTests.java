package com.github.axodenelcorvus.fetcher;

import com.github.axodenelcorvus.model.dto.util.PokeApiJsonParser;
import com.github.axodenelcorvus.model.entry.PokemonEntriesBundle;
import com.github.axodenelcorvus.model.entry.SqlStringResolver;
import com.github.axodenelcorvus.model.entry.SqlTupleSerializable;
import org.junit.jupiter.api.*;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;


@Tag("network")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PayloadTaskTests {
    private final int GENERATION_TEST_ID = 1;
    private final HttpClient TEST_CLIENT = HttpClient.newHttpClient();
    private final PokeApiFetcher fetcher;
    private Set<String> taurosEntriesSerialized = new HashSet<>();
    private Set<String> sneaselEntriesSerialized = new HashSet<>();

    PayloadTaskTests() {
        this.fetcher = new PokeApiFetcher(
                TEST_CLIENT,
                new PokeApiJsonParser(),
                GENERATION_TEST_ID
        );
    }

    @AfterAll
    void close() { TEST_CLIENT.close(); }


    @BeforeAll
    void setupTaurosEntries() {
        //Tauros is all male species, so it only has a male sprite. Still
        //it has no distinct differences between male and female sprites so
        //it has Default descriptor.

        taurosEntriesSerialized
                .add("(128, 128, 'Tauros', 0, 'Normal', null, false, false)");
        taurosEntriesSerialized
                .add("(10250, 128, 'Tauros', 0, 'Fighting', null, false, false)");
        taurosEntriesSerialized
                .add("(128, 'Default', '/universal_default/128.png')");
        taurosEntriesSerialized
                .add("(10250, 'Default Paldea', '/universal_default/128_paldea.png')");
    }

    @BeforeAll
    void setupSneaselEntries() {
        sneaselEntriesSerialized
                .add("(215, 215, 'Sneasel', 4, 'Dark', 'Ice', false, false)");
        sneaselEntriesSerialized
                .add("(10235, 215, 'Sneasel', 4, 'Fighting', 'Poison', false, false)");

        //4 entries pulled to add to database. Both regional variants present distinct
        //male and female forms making Sneasel a special case
        sneaselEntriesSerialized
                .add("(215, 'Male', '/universal_default/128.png')");
        sneaselEntriesSerialized
                .add("(215, 'Female', '/female/128.png')");
        sneaselEntriesSerialized
                .add("(10250, 'Male Hisui', '/universal_default/215_hisui.png')");
        sneaselEntriesSerialized
                .add("(10250, 'Female Hisui', '/female/215_hisui.png')");
    }


    @Test
    @DisplayName("Tauros entries test present")
    void testTaurosEntries() throws Exception {
        Callable<PokemonEntriesBundle> task = new FetchSingleEntriesPayloadTask(fetcher, "tauros");
        PokemonEntriesBundle actualBundle = task.call();

        List<SqlTupleSerializable> serializableEntries = new ArrayList<>();
        serializableEntries.addAll(actualBundle.pokemonEntries());
        serializableEntries.addAll(actualBundle.pokemonSpriteEntries());

        int actualNumPokemonSpriteEntries = actualBundle.pokemonSpriteEntries().size();
        int actualNumPokemonEntries = actualBundle.pokemonEntries().size();

        assertEquals(2, actualNumPokemonEntries);
        assertEquals(2, actualNumPokemonSpriteEntries);

        var sqlStrResolver = new SqlStringResolver();

        for (SqlTupleSerializable serializableEntry : serializableEntries) {
            String pokemonEntrySerialized = serializableEntry.toSqlTuple(sqlStrResolver);
            assertTrue(
                taurosEntriesSerialized.remove(pokemonEntrySerialized),
                () -> "A serialized entry sample does not match one of the expected entry samples or was repeated %nEntry not matched: %s"
                        .formatted(pokemonEntrySerialized)
            );
        }

        assertTrue(
                taurosEntriesSerialized.isEmpty(),
                () -> "These sample entries were not matched: %n%s"
                        .formatted(taurosEntriesSerialized)
                        .replace("),", "),\n")
        );
    }

    @Test
    void testSneaselEntries() {

    }

}