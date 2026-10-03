package com.github.axodenelcorvus.extractor;


import com.github.axodenelcorvus.model.dto.util.PokeApiJsonParser;
import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PokeApiFetcher {

    //This serves as base for which all data is fetched from (using the slug names fetched from this resource)
    private final URI generationURL;

    private static final String API_V2_BASE_PATH = "https://pokeapi.co/api/v2";
    private static final String SPECIES_PATH_FORMATTER = "/pokemon-species/%s/";
    private static final String POKEMON_PATH_FORMATTER = "/pokemon/%s/";

    private final HttpClient client;
    private final PokeApiJsonParser jsonBodyParser;
    private static final int OK_STATUS = 200;


    public PokeApiFetcher(HttpClient httpClient, PokeApiJsonParser jsonParser, int generationResourcePathValue) {
        client = httpClient;
        jsonBodyParser = jsonParser;
        String generationPath = API_V2_BASE_PATH + "/generation/%d/".formatted(generationResourcePathValue);
        generationURL = URI.create(generationPath);
    }

    private static HttpRequest attainRequestToMake(String pokeApiResourcePath) {
        return HttpRequest.newBuilder()
                .uri(URI.create(pokeApiResourcePath))
                .GET()
                .build();
    }

    public PokemonDTO fetchPokemon(String slugName) throws IOException,InterruptedException {
        String requestPath = API_V2_BASE_PATH + SPECIES_PATH_FORMATTER.formatted(slugName);
        HttpRequest pokemonRequest = attainRequestToMake(requestPath);
        HttpResponse<String> resp = client.send(pokemonRequest, HttpResponse.BodyHandlers.ofString());

        validateResponse(resp);

        return jsonBodyParser.parsePokemonBody(resp.body());
    }

    public PokemonSpeciesDTO fetchPokemonSpecies(String slugName) throws IOException,InterruptedException {
        String requestPath = API_V2_BASE_PATH + POKEMON_PATH_FORMATTER.formatted(slugName);
        HttpRequest speciesRequest = attainRequestToMake(requestPath);
        HttpResponse<String> resp = client.send(speciesRequest, HttpResponse.BodyHandlers.ofString());

        validateResponse(resp);

        return jsonBodyParser.parsePokemonSpeciesBody(resp.body());
    }


    public List<String> fetchPokemonSpeciesSlugNames() throws IOException,InterruptedException {
        HttpRequest generationRequest = attainRequestToMake(generationURL.toString());
        HttpResponse<String> resp = client.send(generationRequest, HttpResponse.BodyHandlers.ofString());

        validateResponse(resp);

        return jsonBodyParser.parseSpeciesSlugNames(resp.body());
    }

    private static void validateResponse(HttpResponse<String> resp) {
        if (resp.statusCode() != OK_STATUS){
            var errorMsg = "An unexpected issue occurred " +
                    "communicating with server using %s ".formatted(resp.uri().getPath());
            throw new HttpResponseException(errorMsg, resp.statusCode());
        }
    }

}
