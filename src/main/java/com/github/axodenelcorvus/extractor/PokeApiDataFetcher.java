package com.github.axodenelcorvus.extractor;


import com.github.axodenelcorvus.dto.pokemon.PokemonJsonExtractor;
import com.github.axodenelcorvus.dto.pokemon_species.PokemonSpeciesDTO;
import com.github.axodenelcorvus.dto.pokemon_species.PokemonSpeciesJsonExtractor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PokeApiDataExtractor {

    //This serves as base for which all data is fetched from (using the slug names fetched from this resource)
    private final URI generationURL;

    private static final String SPECIES_URL_FORMATTER = "https://pokeapi.co/api/v2/pokemon-species/%s/";

    private final HttpClient client;
    private final ObjectMapper mapper;
    private final PokemonJsonExtractor pokemonExtractor;
    private final PokemonSpeciesJsonExtractor pokemonSpeciesExtractor;
    private static final int OK = 200;


    public PokeApiDataExtractor(HttpClient httpClient,
                                ObjectMapper jacksonMapper,
                                URI generationResourceToExtractFrom,
                                PokemonJsonExtractor pokemonExtractor,
                                PokemonSpeciesJsonExtractor pokemonSpeciesExtractor
    ) {
        client = httpClient;
        mapper = jacksonMapper;
        generationURL = generationResourceToExtractFrom;
        this.pokemonExtractor = pokemonExtractor;
        this.pokemonSpeciesExtractor = pokemonSpeciesExtractor;
    }

    private HttpRequest attainRequestToMake(String pokeApiResourcePath) {
        return HttpRequest.newBuilder()
                .uri(URI.create(pokeApiResourcePath))
                .GET()
                .build();
    }

    //TODO
//    public PokemonDTO fetchPokemon(String slugName) {
//
//    }

    public PokemonSpeciesDTO fetchPokemonSpecies(String slugName) throws IOException,InterruptedException {
        HttpRequest pokemonSpeciesRequest = attainRequestToMake(String.format(SPECIES_URL_FORMATTER, slugName));
        HttpResponse<String> resp = client.send(pokemonSpeciesRequest, HttpResponse.BodyHandlers.ofString());

        validateResponse(resp);

        JsonNode speciesBody = mapper.readTree(resp.body());

        return pokemonSpeciesExtractor.extractFrom(speciesBody);
    }


    public List<String> fetchPokemonSpeciesSlugNames() throws IOException,InterruptedException {
        HttpRequest generationRequest = attainRequestToMake(generationURL.getPath());
        HttpResponse<String> resp = client.send(generationRequest, HttpResponse.BodyHandlers.ofString());

        validateResponse(resp);

        JsonNode generationBody = mapper.readTree(resp.body());

        List<JsonNode> nameNodes = generationBody
                                        .get("pokemon_species")
                                        .asArray()
                                        .findValues("name");

        return nameNodes
                .stream()
                .map(JsonNode::asString)
                .toList();
    }

    private static void validateResponse(HttpResponse<String> resp) {
        if (resp.statusCode() != OK){
            var errorMsg = "An unexpected issue occurred " +
                    "communicating with server using %s ".formatted(resp.uri().getPath());
            throw new HttpResponseException(errorMsg, resp.statusCode());
        }
    }


}
