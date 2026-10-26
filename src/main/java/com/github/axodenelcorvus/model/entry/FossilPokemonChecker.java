package com.github.axodenelcorvus.model.entry;

import com.github.axodenelcorvus.GameGenerationScope;

public final class FossilPokemonChecker {

    private final int beginGenerationFossilsId;
    private final int endGenerationFossilIds;

    public FossilPokemonChecker(GameGenerationScope generation) {
        switch (generation) {
            case GameGenerationScope.GEN_1:
                beginGenerationFossilsId = 138;
                endGenerationFossilIds = 142;
                break;
            case GameGenerationScope.GEN_3:
                beginGenerationFossilsId = 345;
                endGenerationFossilIds = 348;
                break;
            case GameGenerationScope.GEN_4:
                beginGenerationFossilsId = 408;
                endGenerationFossilIds = 411;
                break;
            case GameGenerationScope.GEN_5:
                beginGenerationFossilsId = 564;
                endGenerationFossilIds = 567;
                break;
            case GameGenerationScope.GEN_6:
                beginGenerationFossilsId = 696;
                endGenerationFossilIds = 699;
                break;
            case GameGenerationScope.GEN_8:
                beginGenerationFossilsId = 880;
                endGenerationFossilIds = 883;
                break;
            //By default, take on range that will always return false during check (since no valid id's can be zero)
            default:
                beginGenerationFossilsId = 0;
                endGenerationFossilIds = 0;
        }
    }

    public boolean checkIfFossil(int dexId) {
        return dexId >= beginGenerationFossilsId && dexId <= endGenerationFossilIds;
    }
}
