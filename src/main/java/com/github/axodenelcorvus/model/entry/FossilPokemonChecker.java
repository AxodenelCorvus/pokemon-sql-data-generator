package com.github.axodenelcorvus.model.entry;

import com.github.axodenelcorvus.SupportedGameGenerationRange;

public final class FossilPokemonChecker {

    private final int beginGenerationFossilsId;
    private final int endGenerationFossilIds;

    public FossilPokemonChecker(int generation) {

        //By default, take on range that will always return false (since no valid id's can be zero)
        int beginGenerationFossilsId = 0;
        int endGenerationFossilIds = 0;

        switch (generation) {
            case (SupportedGameGenerationRange.FIRST_GEN) -> {
                beginGenerationFossilsId = 138;
                endGenerationFossilIds = 142;
            }
            case (3) -> {
                beginGenerationFossilsId = 345;
                endGenerationFossilIds = 348;
            }
            case (4) -> {
                beginGenerationFossilsId = 408;
                endGenerationFossilIds = 411;
            }
            case (5) -> {
                beginGenerationFossilsId = 564;
                endGenerationFossilIds = 567;
            }
            case (6) -> {
                beginGenerationFossilsId = 696;
                endGenerationFossilIds = 699;
            }
            case (8) -> {
                beginGenerationFossilsId = 880;
                endGenerationFossilIds = 883;
            }
            //Current latest generation has no fossil Pokemon, as do some other generations cases
            case (SupportedGameGenerationRange.LATEST_GEN) -> {}
            default -> {}
        }

        this.beginGenerationFossilsId = beginGenerationFossilsId;
        this.endGenerationFossilIds = endGenerationFossilIds;
    }

    public boolean checkIfFossil(int dexId) {
        return dexId >= beginGenerationFossilsId && dexId <= endGenerationFossilIds;
    }
}
