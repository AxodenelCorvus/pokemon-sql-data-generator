package com.github.axodenelcorvus.extractor;


import com.github.axodenelcorvus.model.dto.pokemon.PokemonDTO;
import com.github.axodenelcorvus.model.dto.pokemon.PokemonJsonExtractor;
import com.github.axodenelcorvus.model.dto.pokemon_species.PokemonSpeciesDTO;
import com.github.axodenelcorvus.model.dto.pokemon_species.PokemonSpeciesJsonExtractor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PokeApiDataFetcher {

    //This serves as base for which all data is fetched from (using the slug names fetched from this resource)
    private final URI generationURL;
    private static final String SPECIES_URL_FORMATTER = "https://pokeapi.co/api/v2/pokemon-species/%s/";
    private static final String POKEMON_URL_FORMATTER = "https://pokeapi.co/api/v2/pokemon/%s/";

    private final HttpClient client;
    private final ObjectMapper mapper;
    private final PokemonJsonExtractor pokemonExtractor = new PokemonJsonExtractor();
    private final PokemonSpeciesJsonExtractor pokemonSpeciesExtractor = new PokemonSpeciesJsonExtractor();
    private static final int OK_STATUS = 200;


    public PokeApiDataFetcher(HttpClient httpClient, ObjectMapper jacksonMapper, URI generationResourceToExtractFrom) {
        client = httpClient;
        mapper = jacksonMapper;
        generationURL = generationResourceToExtractFrom;
    }

    private static HttpRequest attainRequestToMake(String pokeApiResourcePath) {
        return HttpRequest.newBuilder()
                .uri(URI.create(pokeApiResourcePath))
                .GET()
                .build();
    }

    public PokemonDTO fetchPokemon(String slugName) throws IOException,InterruptedException {
        HttpRequest pokemonRequest = attainRequestToMake(String.format(POKEMON_URL_FORMATTER, slugName));
        HttpResponse<String> resp = client.send(pokemonRequest, HttpResponse.BodyHandlers.ofString());

        validateResponse(resp);

        JsonNode pokemonBody = mapper.readTree(resp.body());

        return pokemonExtractor.extractFrom(pokemonBody);
    }

    public PokemonSpeciesDTO fetchPokemonSpecies(String slugName) throws IOException,InterruptedException {
        HttpRequest pokemonSpeciesRequest = attainRequestToMake(String.format(SPECIES_URL_FORMATTER, slugName));
        HttpResponse<String> resp = client.send(pokemonSpeciesRequest, HttpResponse.BodyHandlers.ofString());

        validateResponse(resp);

        JsonNode speciesBody = mapper.readTree(resp.body());

        return pokemonSpeciesExtractor.extractFrom(speciesBody);
    }


    public List<String> fetchPokemonSpeciesSlugNames() throws IOException,InterruptedException {
        HttpRequest generationRequest = attainRequestToMake(generationURL.toString());

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
        if (resp.statusCode() != OK_STATUS){
            var errorMsg = "An unexpected issue occurred " +
                    "communicating with server using %s ".formatted(resp.uri().getPath());
            throw new HttpResponseException(errorMsg, resp.statusCode());
        }
    }

}
