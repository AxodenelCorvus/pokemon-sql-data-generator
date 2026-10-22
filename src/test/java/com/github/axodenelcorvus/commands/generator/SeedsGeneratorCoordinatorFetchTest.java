package com.github.axodenelcorvus.commands.generator;

import com.github.axodenelcorvus.fetcher.PokeApiFetcher;
import com.github.axodenelcorvus.fetcher.SingleAlternativeFormMapProvider;
import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;
import com.github.axodenelcorvus.model.dto.PokemonVarietyDTO;
import com.github.axodenelcorvus.model.dto.parser.PokeApiJsonParser;
import com.github.axodenelcorvus.model.entry.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.github.axodenelcorvus.commands.generator.ResourceDtoGatherer.RESOURCES_ROOT_DIR;
import static org.junit.jupiter.api.Assertions.*;

@Tag("network")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SeedsGeneratorCoordinatorFetchTest {
    private final HttpClient TEST_CLIENT = HttpClient.newHttpClient();
    private final int GENERATION_OF_GIRATINA = 4;

    private final PokeApiJsonParser jsonParserUsed = new PokeApiJsonParser();

    private PokemonEntriesBundle expectedPokemonEntriesBundle;
    private PokemonEntriesBundle actualPokemonEntriesBundle;
    private Set<PokemonSpriteEntry> actualPokemonSpriteEntriesSet;

    private final List<String> speciesToFetch = List.of(
            "bulbasaur",
            "charizard",
            "blissey",
            "corsola",
            "darkrai",
            "geodude",
            "giratina",
            "ho-oh",
            "raticate",
            "meowth",
            "pikachu",
            "sneasel"
    );

    private final SeedsGeneratorCoordinator generatorCoordinator;

    //Setup SeedsGeneratorCoordinator

    SeedsGeneratorCoordinatorFetchTest() {
        Map<Integer, String> singleAltFormDescriptorsByPokemonId =
                                SingleAlternativeFormMapProvider.from(GENERATION_OF_GIRATINA);

        PokeApiFetcher pokeApiFetcher = new PokeApiFetcher(
                TEST_CLIENT,
                jsonParserUsed,
                GENERATION_OF_GIRATINA
        );

        this.generatorCoordinator = new SeedsGeneratorCoordinator(
                null, //Not in use for these tests
                pokeApiFetcher,
                singleAltFormDescriptorsByPokemonId
        );
    }

    @BeforeAll
    void setup() throws IOException {
        //OBTAIN DESERIALIZED TEST RESOURCES

        ResourceDtoGatherer rsrcGatherer =
                    new ResourceDtoGatherer(jsonParserUsed);

        Files.walkFileTree(RESOURCES_ROOT_DIR, rsrcGatherer);

        List<PokemonSpeciesDTO> samplePokemonSpeciesDtos = rsrcGatherer
                .getSampleResourcePokemonSpecies()
                .stream()
                .sorted(Comparator.comparing(PokemonSpeciesDTO::name))
                .toList();

        List<TaggedPokemonDTO> samplePokemonDtos = rsrcGatherer
                .getSampleResourcePokemon()
                .stream()
                .sorted(Comparator.comparing((dto) -> dto.value().slugName()))
                .toList();


        assertFalse(samplePokemonDtos.isEmpty(),
                "No deserialized PokemonDTO's from resource files gathered");
        assertFalse(samplePokemonSpeciesDtos.isEmpty(),
                "No deserialized PokemonSpeciesDTO's from resource files gathered\"");

        assertTrue(
                samplePokemonSpeciesDtos.size() == speciesToFetch.size(),
                "Number of PokemonSpeciesDTO's deserialized does not\n" +
                        "match number of species to fetch for tests"
        );


        //PRODUCE EXPECTED END RESULT BUNDLE OF TEST
        this.setupExpectedBundle(samplePokemonSpeciesDtos, samplePokemonDtos);
    }

    /*
    Explanation of setup method:

    -Left param is sorted alphabetically.
    -Right param is sorted alphabetically

    -There is one-to-many relationship between left and right (one species to many subspecies)
    by sorting

    corsola - TaggedDTO from GalarCorsolaSample.json <-@%
            \ TaggedDTO from CorsolaSample.json
    meowth   - TaggedDTO from AlolaMeowthSample.json   <- @$
            \ TaggedDTO from GalarMeowthSample.json
           \ TaggedDTO from HisuiMeowthSample.json

    @%
        -Outer loop at Corsola species starts new subSpeciesPokemonByRegion mapping
        -The slug variety names for Corsola are gathered into a set.
        -Index i is used to grab the slug name of current Pokemon in collection of Pokemon DTO samples
        -for-loop.
            1.
                -Since corsola-galar meets a slug in set, loop not broken (continues)
                -A new entry to subSpeciesPokemonByRegion is made: RegionalForm.GALAR -> Galar DTO
            2.
                -Since corsola meets a slug in set, loop not broken (continues)
                -A new entry to subSpeciesPokemonByRegion is made: RegionalForm.STANDARD -> Std DTO
            3.
                -Since meowth-aloala is NOT in set, loop broken out of and i does not increment. i is at
                start of next species section (due to sorting done previously). i stops @$
          -The current "species" group of Corsola is bundled together using base species and subspecies
          by region, which is then collected in a list of PokemonEntryBundle's.

    @$
        -This point i in inner loop stopped at boundary end of Corsola section and is at start of Mewoth's section
        (due to sorting done previously). Same process as before starts again.
    */
    private void setupExpectedBundle(
            List<PokemonSpeciesDTO> samplePokemonSpeciesDtos,
            List<TaggedPokemonDTO> samplePokemonDtos
    ) {
        List<PokemonEntriesBundle> listOfPkmSpeciesBundled = new ArrayList<>();
        int i = 0; //Index used to walk through tagged samplePokemonDtos
        EnumMap<RegionalForm, PokemonDTO> subSpeciesPokemonByRegion;

        for (PokemonSpeciesDTO samplePokemonSpeciesBase : samplePokemonSpeciesDtos) {
            subSpeciesPokemonByRegion = new EnumMap<>(RegionalForm.class);

            Set<String> varietySlugNames = samplePokemonSpeciesBase
                    .varieties()
                    .stream()
                    .map(PokemonVarietyDTO::slugName)
                    .collect(Collectors.toUnmodifiableSet());

            for (i = i; i < samplePokemonDtos.size(); i++) {
                TaggedPokemonDTO currTaggedPokemonDTO = samplePokemonDtos.get(i);
                String currPokemonSlugName = currTaggedPokemonDTO.value().slugName();

                if (!varietySlugNames.contains(currPokemonSlugName))
                    break;

                subSpeciesPokemonByRegion.put(
                        currTaggedPokemonDTO.regionalTag(),
                        currTaggedPokemonDTO.value()
                );
            }

            var currPokemonSpeciesBundle = PokemonEntriesBundle
                    .fromSpecies(samplePokemonSpeciesBase, subSpeciesPokemonByRegion);

            if (samplePokemonSpeciesBase.name().equals("Giratina")) {
                var additionalAltSpriteEntry = new PokemonSpriteEntry(487, "Origin");
                currPokemonSpeciesBundle.pokemonSpriteEntries().add(additionalAltSpriteEntry);
            }

            listOfPkmSpeciesBundled.add(currPokemonSpeciesBundle);
        }

        assertEquals(
                speciesToFetch.size(),
                listOfPkmSpeciesBundled.size(),
                "Number of species to fetch and the number of Pokemon species bundles expected do not match"
        );

        expectedPokemonEntriesBundle =
                PokemonEntriesBundle.flattenBundles(listOfPkmSpeciesBundled);
    }

    @Test
    @Order(1)
    @DisplayName("Pokemon record entries fetched and compiled match expected entries")
    void testPokemonEntriesFormed() throws IOException, ExecutionException, InterruptedException {
        actualPokemonEntriesBundle = generatorCoordinator.fetchEntries(speciesToFetch);

        Set<PokemonEntry> expectedPokemonEntriesSet = expectedPokemonEntriesBundle
                .pokemonEntries()
                .stream()
                .collect(Collectors.toUnmodifiableSet());
        Set<PokemonEntry> actualPokemonEntriesSet = actualPokemonEntriesBundle
                .pokemonEntries()
                .stream()
                .collect(Collectors.toUnmodifiableSet());

        assertEquals(expectedPokemonEntriesSet, actualPokemonEntriesSet);
    }

    @Test
    @Order(2)
    @DisplayName("Pokemon sprite record entries fetched and compiled match expected entries")
    void testPokemonSpriteEntriesFormed() {
        Set<PokemonSpriteEntry> expectedPokemonSpriteEntriesSet = expectedPokemonEntriesBundle
                .pokemonSpriteEntries()
                .stream()
                .collect(Collectors.toUnmodifiableSet());

        actualPokemonSpriteEntriesSet = actualPokemonEntriesBundle
                .pokemonSpriteEntries()
                .stream()
                .collect(Collectors.toUnmodifiableSet());

        assertEquals(expectedPokemonSpriteEntriesSet, actualPokemonSpriteEntriesSet);
    }

    @Test
    @Order(3)
    @DisplayName("Alolan Raticate entries do NOT contain false entries")
    void testActualAlolaRaticateEntries() {
        Set<PokemonSpriteEntry> invalidGenderEntries = Set.of(
                new PokemonSpriteEntry(10092, GenderSpriteForm.FEMALE, RegionalForm.ALOLA, 25),
                new PokemonSpriteEntry(10092, GenderSpriteForm.MALE, RegionalForm.ALOLA, 25)
        );

        assertFalse(actualPokemonSpriteEntriesSet.containsAll(invalidGenderEntries),
                "Invalid sprite entries found for Alolan Raticate: Male & Female entries present");
    }

    @Test
    @Order(4)
    @DisplayName("Standard Raticate entries are correct")
    void testActualStandardRaticateEntries() {
        Set<PokemonSpriteEntry> validGenderEntries = Set.of(
                new PokemonSpriteEntry(25, GenderSpriteForm.FEMALE, RegionalForm.STANDARD, 25),
                new PokemonSpriteEntry(25, GenderSpriteForm.MALE, RegionalForm.STANDARD, 25)
        );

        assertTrue(actualPokemonSpriteEntriesSet.containsAll(validGenderEntries),
                () -> "Missing these sprite entries for standard Raticate: " + validGenderEntries);
    }

}