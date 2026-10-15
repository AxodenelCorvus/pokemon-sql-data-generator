package com.github.axodenelcorvus.commands.generator;

import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;
import com.github.axodenelcorvus.model.dto.util.PokeApiJsonParser;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assumptions.abort;


class ResourceDtoGatherer extends SimpleFileVisitor<Path> {
    private final PokeApiJsonParser pokeParser;
    private List<PokemonDTO> rsrcPokemonDtos = new ArrayList<>();
    private List<PokemonSpeciesDTO> rsrcPokemonSpecies = new ArrayList<>();

    private final Path rootRsrcDir;
    private final Path[] targetDirs;
    private int targetDirsVisited = 0;

    ResourceDtoGatherer(PokeApiJsonParser pokeParser, Path rootRsrcDir, Path[] targetDirs) {
        this.pokeParser = pokeParser;
        this.rootRsrcDir = rootRsrcDir;
        this.targetDirs = targetDirs;
    }

    public List<PokemonDTO> getRsrcPokemonDtos() {
        return List.copyOf(rsrcPokemonDtos);
    }

    public List<PokemonSpeciesDTO> getRsrcPokemonSpeciesDtos() {
        return List.copyOf(rsrcPokemonSpecies);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path currDir, BasicFileAttributes ignored) {
        if (targetDirsVisited == targetDirs.length)
            return FileVisitResult.TERMINATE;

        if (currDir.equals(rootRsrcDir))
            return FileVisitResult.CONTINUE;

        for (Path targetDir : targetDirs) {
            if (currDir.equals(targetDir)) {
                targetDirsVisited++;
                return FileVisitResult.CONTINUE;
            }
        }

        return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (exc != null)
            abort(exc.getMessage());

        targetDirsVisited++;

        return FileVisitResult.TERMINATE;
    }

    @Override
    public FileVisitResult visitFile(Path testJsonFile,
                                     BasicFileAttributes fileAttrs) throws IOException {
        if (!fileAttrs.isRegularFile())
            return FileVisitResult.CONTINUE;

        String fileName = testJsonFile.getFileName().toString();

        if (!fileName.endsWith("Sample.json"))
            return FileVisitResult.CONTINUE;

        List<String> lines = Files.readAllLines(testJsonFile);

        String jsonBody = lines
                .stream()
                .reduce("", (currBody, currLine) -> currBody + currLine);

        String parentName = testJsonFile.getParent().toString();

        if (parentName.equals("pokemon"))
            rsrcPokemonDtos.add(pokeParser.parsePokemonBody(jsonBody));
        else
            rsrcPokemonSpecies.add(pokeParser.parsePokemonSpeciesBody(jsonBody));

        return FileVisitResult.CONTINUE;
    }

}
