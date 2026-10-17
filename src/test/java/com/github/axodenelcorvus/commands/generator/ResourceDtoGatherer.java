package com.github.axodenelcorvus.commands.generator;

import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;
import com.github.axodenelcorvus.model.dto.util.PokeApiJsonParser;
import com.github.axodenelcorvus.model.entry.RegionalForm;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class ResourceDtoGatherer extends SimpleFileVisitor<Path> {
    private final PokeApiJsonParser pokeParser;
    private List<PokemonSpeciesDTO> rsrcPokemonSpecies = new ArrayList<>();
    private List<TaggedPokemonDTO> rsrcPokemon = new ArrayList<>();

    public static final Path RESOURCES_ROOT_DIR = Path.of(
            "src", "test", "resources", "dto_collection_samples"
    );

    public static final int MAX_TARGET_DIRS = 2;
    private static final Path TRGT_POKEMON_DIR =
                                RESOURCES_ROOT_DIR.resolve("pokemon");
    private static final Path TRGT_POKEMON_SPECIES_DIR =
                                RESOURCES_ROOT_DIR.resolve("pokemon_species");
    private int targetDirsVisited = 0;

    private static final Pattern TARGET_FILE_PATTERN =
            Pattern.compile("(Galar|Alola|Hisui|Paldea)?[A-Z][A-Za-z0-9_-]+Sample\\.json");

    ResourceDtoGatherer(PokeApiJsonParser pokeParser) {
        this.pokeParser = pokeParser;
    }

    public List<PokemonSpeciesDTO> getSampleResourcePokemonSpecies() {
        return List.copyOf(rsrcPokemonSpecies);
    }

    public List<TaggedPokemonDTO> getSampleResourcePokemon() {
        return List.copyOf(rsrcPokemon);
    }


    @Override
    public FileVisitResult preVisitDirectory(Path currDir, BasicFileAttributes ignored) throws IOException {
        if (targetDirsVisited == MAX_TARGET_DIRS)
            return FileVisitResult.TERMINATE;

        if (currDir.equals(RESOURCES_ROOT_DIR))
            return FileVisitResult.CONTINUE;

        if (currDir.equals(TRGT_POKEMON_DIR) || currDir.equals(TRGT_POKEMON_SPECIES_DIR)) {
            targetDirsVisited++;
            return FileVisitResult.CONTINUE;
        }

        throw new FileSystemException(currDir.toString(), null, "Unexpected nested directory not supported for DTO Sample gathering");
    }

    @Override
    public FileVisitResult visitFile(Path testJsonFile,
                                     BasicFileAttributes fileAttrs) throws IOException {

        String fileName = testJsonFile.getFileName().toString();

        Matcher matcher = TARGET_FILE_PATTERN.matcher(fileName);


        if (!matcher.matches())
            return FileVisitResult.CONTINUE;

        String jsonBody = Files
                .readAllLines(testJsonFile)
                .stream()
                .reduce("", (currBody, currLine) -> currBody + currLine);

        Path parent = testJsonFile.getParent();

        if (parent.endsWith("pokemon_species"))
            rsrcPokemonSpecies.add(pokeParser.parsePokemonSpeciesBody(jsonBody));

        if (parent.endsWith("pokemon")) {
            PokemonDTO pokemonDTO = pokeParser.parsePokemonBody(jsonBody);

            String regionCandidateFromPrefix = matcher.group(1);

            RegionalForm regionalFormTag = switch(regionCandidateFromPrefix) {
                case("Galar")  -> RegionalForm.GALAR;
                case("Alola")  -> RegionalForm.ALOLA;
                case("Hisui")  -> RegionalForm.HISUI;
                case("Paldea") -> RegionalForm.PALDEA;
                case null      -> RegionalForm.STANDARD;
                default        -> RegionalForm.STANDARD;
            };

            rsrcPokemon.add(new TaggedPokemonDTO(pokemonDTO, regionalFormTag));
        }

        return FileVisitResult.CONTINUE;

    }

}
