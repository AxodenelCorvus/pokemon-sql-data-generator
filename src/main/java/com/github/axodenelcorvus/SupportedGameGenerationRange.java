package com.github.axodenelcorvus;

public final class SupportedGameGenerationRange {
    private SupportedGameGenerationRange() {}

    //Supported range of generations
    public static final int FIRST_GEN = 1;
    public static final int LATEST_GEN = 9;

    public static boolean isValidGeneration(int generation) {
        return generation >= FIRST_GEN && generation <= LATEST_GEN;
    }

    public static String getOutOfRangeErrMsg() {
        return "Specified generation not in supported range %d to %d"
                    .formatted(FIRST_GEN, LATEST_GEN);
    }
}
