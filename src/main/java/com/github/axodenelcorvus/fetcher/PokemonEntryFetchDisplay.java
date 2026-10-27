package com.github.axodenelcorvus.fetcher;

import com.github.axodenelcorvus.model.entry.PokemonEntry;

public final class PokemonEntryFetchDisplay {

    private PokemonEntryFetchDisplay() {}

    private static final String ENTRY_FORMAT;

    //Length of output entry output strings up to middle delimiter (up to 2nd '|')
    public static final int END_BOUND_OF_MID_COLUMN;

    public static final int TOTAL_LENGTH_OF_ENTRY_DISPLAYS;

    static {
        int TAB_LENGTH = 8;

        String entryFormat = " ".repeat(TAB_LENGTH);

        entryFormat += "Primary Key: %-5d | Dex ID: %04d | Type1: %-8s | Type2: %-8s";

        ENTRY_FORMAT = entryFormat;

        String madeUpPackedSample = entryFormat.formatted(10000, 1000, "Fighting", "Fighting");

        END_BOUND_OF_MID_COLUMN = madeUpPackedSample.indexOf(" T");

        //Include delimiters and whitespaces in length
        TOTAL_LENGTH_OF_ENTRY_DISPLAYS = madeUpPackedSample.length();
    }

    public static String getHeaderDisplay() {
        String entriesHeaderContentLeftPiece = "(--ENTRIES ";
        String entriesHeaderContentRightPiece = "FETCHED--)";


        StringBuilder entriesHeaderBuilder = new StringBuilder();

        int entriesHeaderLeftPadLength = END_BOUND_OF_MID_COLUMN - entriesHeaderContentLeftPiece.length();

        entriesHeaderBuilder
                .repeat(' ', entriesHeaderLeftPadLength)
                .append(entriesHeaderContentLeftPiece).append(entriesHeaderContentRightPiece);

        return entriesHeaderBuilder.toString();
    }

    public static String formatDisplayFrom(PokemonEntry pokemonEntry) {
        String secondaryTypeDisplayVal = pokemonEntry.getSecondaryType();

        if (secondaryTypeDisplayVal == null)
            secondaryTypeDisplayVal = "N/A";

        return ENTRY_FORMAT.formatted(
                pokemonEntry.getId(),
                pokemonEntry.getNationalDexId(),
                pokemonEntry.getPrimaryType(),
                secondaryTypeDisplayVal
        );
    }
}