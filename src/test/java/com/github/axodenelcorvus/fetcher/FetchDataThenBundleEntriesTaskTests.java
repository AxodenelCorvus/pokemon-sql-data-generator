package com.github.axodenelcorvus.fetcher;

import com.github.axodenelcorvus.model.dto.util.PokeApiJsonParser;
import com.github.axodenelcorvus.model.entry.PokemonEntriesBundle;
import com.github.axodenelcorvus.model.entry.SqlStringResolver;
import com.github.axodenelcorvus.model.entry.SqlTupleSerializable;
import org.junit.jupiter.api.*;

import java.net.http.HttpClient;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;


@Tag("network")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FetchDataThenBundleEntriesTaskTests {
    private final int GENERATION_TEST_ID = 1;
    private final HttpClient TEST_CLIENT = HttpClient.newHttpClient();
    private final PokeApiFetcher fetcher;


    private Set<String> taurosEntriesSerialized = new HashSet<>();
    private Set<String> sneaselEntriesSerialized = new HashSet<>();
    private Set<String> genFourSingleAltFormEntriesSerialized = new HashSet<>();

    private final String MORE_ENTRIES_THAN_EXPECTED_MSG =
            "More entries than expected, possible duplicate entries *may* be present as well";

    private void assertAllEntriesMatched(Set<String> setOfRemainingPokemonEntries) {
        assertTrue(
                setOfRemainingPokemonEntries.isEmpty(),
                () -> "%d sample entries were not matched: %n%s"
                        .formatted(setOfRemainingPokemonEntries.size(),
                                setOfRemainingPokemonEntries)
                        .replace("),", "),\n")
        );
    }

    private Supplier<String> noEntryMatchInExpectedSetMsg(String serializedEntryNotMatched) {
        return () -> """
                %nA serialized Pokemon entry sample doesn't match one of
                the expected entry samples or was repeated
                _______________________________________________________
                Entry not matched: %s
                """.formatted(serializedEntryNotMatched);
    }

    FetchDataThenBundleEntriesTaskTests() {
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
        //it has no distinct differences between male and female sprites so uses descriptor: "Default"

        taurosEntriesSerialized
                .add("(128, 128, 'Tauros', 0, 'Normal', null, false, false)");
        taurosEntriesSerialized
                .add("(10250, 128, 'Tauros', 0, 'Fighting', null, false, false)");
        taurosEntriesSerialized
                .add("(128, 'Default', '/universal_default/128.png')");
        taurosEntriesSerialized
                .add("(10250, 'Default Paldea', '/universal_default/128_paldea.png')");
    }

    //6 entries pulled to add to database with 3 total API calls (one to species and then to each variant in the group).
    //Both regional variants present distinct male and female forms making Sneasel a special case
    @BeforeAll
    void setupSneaselEntries() {
        sneaselEntriesSerialized
                .add("(215, 215, 'Sneasel', 4, 'Dark', 'Ice', false, false)");
        sneaselEntriesSerialized
                .add("(10235, 215, 'Sneasel', 4, 'Fighting', 'Poison', false, false)");

        sneaselEntriesSerialized
                .add("(215, 'Male', '/universal_default/215.png')");
        sneaselEntriesSerialized
                .add("(215, 'Female', '/female/215.png')");
        sneaselEntriesSerialized
                .add("(10235, 'Male Hisui', '/universal_default/215_hisui.png')");
        sneaselEntriesSerialized
                .add("(10235, 'Female Hisui', '/female/215_hisui.png')");
    }
    
    @BeforeAll
    void setupSingleAltFormsForGeneration4() {
        //The 3rd .add() of each segment represents the single alternative form

        //One SHAYMIN entry to its significant sprite record entries
        genFourSingleAltFormEntriesSerialized
                .add("(492, 492, 'Shaymin', null, 'Grass', null, true, false)");
        genFourSingleAltFormEntriesSerialized
                .add("(492, 'Default', '/universal_default/492.png')");
        genFourSingleAltFormEntriesSerialized
                .add("(492, 'Sky', '/universal_default/492_alt_form.png')");

        //One GIRATINA entry to its significant sprite record entries
        genFourSingleAltFormEntriesSerialized
                .add("(487, 487, 'Giratina', null, 'Ghost', 'Dragon', false, true)");
        genFourSingleAltFormEntriesSerialized
                .add("(487, 'Default', '/universal_default/487.png')");
        genFourSingleAltFormEntriesSerialized
                .add("(487, 'Origin', '/universal_default/487_alt_form.png')");

        //One PALKIA entry to its significant sprite record entries
        genFourSingleAltFormEntriesSerialized
                .add("(484, 484, 'Palkia', null, 'Water', 'Dragon', false, true)");
        genFourSingleAltFormEntriesSerialized
                .add("(484, 'Default', '/universal_default/484.png')");
        genFourSingleAltFormEntriesSerialized
                .add("(484, 'Origin', '/universal_default/484_alt_form.png')");

        //One DIALGA entry to its significant sprite record entries
        genFourSingleAltFormEntriesSerialized
                .add("(483, 483, 'Dialga', null, 'Steel', 'Dragon', false, true)");
        genFourSingleAltFormEntriesSerialized
                .add("(483, 'Default', '/universal_default/483.png')");
        genFourSingleAltFormEntriesSerialized
                .add("(483, 'Origin', '/universal_default/483_alt_form.png')");
    }


    @Test
    @DisplayName("Tauros entries fetch task")
    void testTaurosEntries() throws Exception {
        Callable<PokemonEntriesBundle> task = new FetchDataThenBundleEntriesTask(fetcher, "tauros", Map.of());
        PokemonEntriesBundle actualBundle = task.call();

        List<SqlTupleSerializable> serializableEntries = new ArrayList<>();
        serializableEntries.addAll(actualBundle.pokemonEntries());
        serializableEntries.addAll(actualBundle.pokemonSpriteEntries());

        final int actualNumPokemonSpriteEntries = actualBundle.pokemonSpriteEntries().size();
        final int actualNumPokemonEntries = actualBundle.pokemonEntries().size();

        assertFalse(2 > actualNumPokemonEntries, MORE_ENTRIES_THAN_EXPECTED_MSG);
        assertFalse(2 > actualNumPokemonSpriteEntries, MORE_ENTRIES_THAN_EXPECTED_MSG);

        var sqlStrResolver = new SqlStringResolver();

        for (SqlTupleSerializable serializableEntry : serializableEntries) {
            String pokemonEntrySerialized = serializableEntry.toSqlTuple(sqlStrResolver);
            assertTrue(
                taurosEntriesSerialized.remove(pokemonEntrySerialized),
                    noEntryMatchInExpectedSetMsg(pokemonEntrySerialized)
            );
        }

        assertAllEntriesMatched(taurosEntriesSerialized);
    }

    @Test
    @DisplayName("Sneasel entries fetch task")
    void testSneaselEntries() throws Exception {
        Callable<PokemonEntriesBundle> task = new FetchDataThenBundleEntriesTask(fetcher, "sneasel", Map.of());
        PokemonEntriesBundle actualBundle = task.call();

        List<SqlTupleSerializable> serializableEntries = new ArrayList<>();
        serializableEntries.addAll(actualBundle.pokemonEntries());
        serializableEntries.addAll(actualBundle.pokemonSpriteEntries());

        final int actualNumPokemonSpriteEntries = actualBundle.pokemonSpriteEntries().size();
        final int actualNumPokemonEntries = actualBundle.pokemonEntries().size();

        assertFalse(2 > actualNumPokemonEntries, MORE_ENTRIES_THAN_EXPECTED_MSG);
        assertFalse(4 > actualNumPokemonSpriteEntries, MORE_ENTRIES_THAN_EXPECTED_MSG);

        var sqlStrResolver = new SqlStringResolver();

        for (SqlTupleSerializable serializableEntry : serializableEntries) {
            String pokemonEntrySerialized = serializableEntry.toSqlTuple(sqlStrResolver);
            assertTrue(
                    sneaselEntriesSerialized.remove(pokemonEntrySerialized),
                    noEntryMatchInExpectedSetMsg(pokemonEntrySerialized)
            );
        }

        assertAllEntriesMatched(sneaselEntriesSerialized);
    }

    @Test
    @DisplayName("Fetch tasks for generation 4 entries w/ single alternative forms that are determinable")
    void testGenerationFourSpecialCases() throws Exception {
        int generationInTestUse = 4;
        Map<Integer, String> singleAltFormDescriptorsById = SingleAlternativeFormMapProvider.from(generationInTestUse);
        List<String> slugNamesForTasks = List.of("shaymin", "giratina", "dialga", "palkia");
        List<PokemonEntriesBundle> entriesBundles = new ArrayList<>();

        for (String slugName : slugNamesForTasks) {
            Callable<PokemonEntriesBundle> fetchTaskForCurrPokemonEntries =
                    new FetchDataThenBundleEntriesTask(fetcher, slugName, singleAltFormDescriptorsById);
            entriesBundles.add(fetchTaskForCurrPokemonEntries.call());
        }

        PokemonEntriesBundle actualBundle = PokemonEntriesBundle.flattenBundles(entriesBundles);

        final int actualNumPokemonSpriteEntries = actualBundle.pokemonSpriteEntries().size();
        final int actualNumPokemonEntries = actualBundle.pokemonEntries().size();

        assertFalse(4 > actualNumPokemonEntries, MORE_ENTRIES_THAN_EXPECTED_MSG);
        assertFalse(8 > actualNumPokemonSpriteEntries, MORE_ENTRIES_THAN_EXPECTED_MSG);

        List<SqlTupleSerializable> serializableEntries = new ArrayList<>();
        serializableEntries.addAll(actualBundle.pokemonEntries());
        serializableEntries.addAll(actualBundle.pokemonSpriteEntries());

        var sqlStrResolver = new SqlStringResolver();

        for (SqlTupleSerializable serializableEntry : serializableEntries) {
            String pokemonEntrySerialized = serializableEntry.toSqlTuple(sqlStrResolver);
            assertTrue(
                    genFourSingleAltFormEntriesSerialized.remove(pokemonEntrySerialized),
                    noEntryMatchInExpectedSetMsg(pokemonEntrySerialized)
            );
        }

        assertAllEntriesMatched(genFourSingleAltFormEntriesSerialized);
    }

}