package com.github.axodenelcorvus.model.entry;

import com.github.axodenelcorvus.model.DtoSampleProvider;
import com.github.axodenelcorvus.model.dto.PokemonDTO;
import com.github.axodenelcorvus.model.dto.PokemonSpeciesDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


class PokemonSpriteEntryFlowTests {
    private final DtoSampleProvider dtoSampleProvider = new DtoSampleProvider();

    @Test
    @DisplayName("Sample standard test flow with Alolan regional form")
    void testSampleCreationFlow() {
        var ninetalesAlolaSampleId = 10104;
        PokemonDTO pokemonDTO = dtoSampleProvider
                    .getPokemonDTO(ninetalesAlolaSampleId);
        PokemonSpeciesDTO pokemonSpeciesDTO = dtoSampleProvider
                    .getPokemonSpeciesDTO(ninetalesAlolaSampleId);

        var expectedImgFileEndOfPath = "%d_alola.png".formatted(pokemonSpeciesDTO.dexId());

        String[] slugNameSegments =  pokemonDTO.slugName().split("-");


        assertEquals("alola", slugNameSegments[1]);
        RegionalForm alola = RegionalForm.ALOLA;

        assertFalse(pokemonSpeciesDTO.hasGenderDifferences());
        GenderSpriteForm none = GenderSpriteForm.NONE;


        PokemonSpriteEntry pokemonSpriteEntry = new PokemonSpriteEntry(ninetalesAlolaSampleId, none, alola, pokemonSpeciesDTO.dexId());

        String[] actualFormDescriptorSegments = pokemonSpriteEntry.getSpriteFormDescriptor().split(" ");
        String[] actualStaticImgResourcePath = pokemonSpriteEntry.getStaticImgResourcePath()
                                                            .substring(1).split("/");

        assertEquals(ninetalesAlolaSampleId, pokemonSpriteEntry.getForeignKey());

        assertEquals("Default", actualFormDescriptorSegments[0]);
        assertEquals("Alola", actualFormDescriptorSegments[1]);
        assertEquals(2, actualFormDescriptorSegments.length,
                            "More segments than expected or unexpected whitespace issues for descriptor: %s".formatted(Arrays.toString(actualFormDescriptorSegments)));

        assertEquals('/', pokemonSpriteEntry.getStaticImgResourcePath().charAt(0));
        assertEquals("universal_default", actualStaticImgResourcePath[0]);
        assertEquals(expectedImgFileEndOfPath, actualStaticImgResourcePath[1]);
        assertEquals(2, actualStaticImgResourcePath.length,
                    "More segments than expected for static image resource path");

    }


    @Test
    @DisplayName("Check Pokemon Sprite Entry against Hisui Sneasel case")
    void testHisuiEdgeCase() {
        var sneaselHisuiSampleId = 10235;
        PokemonDTO pokemonDTO = dtoSampleProvider
                .getPokemonDTO(sneaselHisuiSampleId);
        PokemonSpeciesDTO pokemonSpeciesDTO = dtoSampleProvider
                .getPokemonSpeciesDTO(sneaselHisuiSampleId);

        var expectedImgFileEndOfPath = "%d_hisui.png".formatted(pokemonSpeciesDTO.dexId());

        String[] slugNameSegments =  pokemonDTO.slugName().split("-");

        RegionalForm hisui = RegionalForm.HISUI;
        assertEquals("hisui", slugNameSegments[1]);

        assertTrue(pokemonSpeciesDTO.hasGenderDifferences());
        GenderSpriteForm none = GenderSpriteForm.FEMALE; //Will use female case, but male sprite case also exists

        assertEquals("hisui", slugNameSegments[1]);

        PokemonSpriteEntry pokemonSpriteEntry = new PokemonSpriteEntry(sneaselHisuiSampleId, none, hisui, pokemonSpeciesDTO.dexId());

        String[] actualFormDescriptorSegments = pokemonSpriteEntry.getSpriteFormDescriptor().split(" ");
        String[] actualStaticImgResourcePath = pokemonSpriteEntry.getStaticImgResourcePath()
                                                            .substring(1).split("/");

        assertEquals(sneaselHisuiSampleId, pokemonSpriteEntry.getForeignKey());

        assertEquals("Female", actualFormDescriptorSegments[0]);
        assertEquals("Hisui", actualFormDescriptorSegments[1]);
        assertEquals(2, actualFormDescriptorSegments.length,
                        "More segments than expected or unexpected whitespace issues for descriptor: %s".formatted(Arrays.toString(actualFormDescriptorSegments)));

        assertEquals('/', pokemonSpriteEntry.getStaticImgResourcePath().charAt(0));
        assertEquals("female", actualStaticImgResourcePath[0]);
        assertEquals(expectedImgFileEndOfPath, actualStaticImgResourcePath[1]);
        assertEquals(2, actualStaticImgResourcePath.length,
                "More segments than expected for static image resource path");

    }

}
