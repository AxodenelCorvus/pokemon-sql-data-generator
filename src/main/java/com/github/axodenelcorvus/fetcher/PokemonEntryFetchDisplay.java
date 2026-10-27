package com.github.axodenelcorvus.fetcher;

import com.github.axodenelcorvus.model.entry.PokemonEntry;

public final class PokemonEntryFetchDisplay {

    private PokemonEntryFetchDisplay() {}

    private static final String ENTRY_FORMAT;

    //Length of output entry output strings up to middle delimiter with delimiter included in length
    //For made up example:
    //"        Primary Key: 10000 | Dex ID: 1111 | "
    public static final int END_BOUND_OF_MID_DELIMITER;

    public static final int TOTAL_LENGTH_OF_ENTRY_DISPLAYS;

    static {
        int tabLength = 8;

        String[] fieldLabels = { "Primary Key: ", "Dex ID: ", "Type1: ", "Type2: " };
        int[] fieldSpannedLengths = { 5, 4, 8, 8 };

        String COL_DELIMITER = " | ";

        int halfOfEntryFieldsDisplayLen = tabLength;

        int middleBoundaryOfFieldLabels = fieldLabels.length / 2;

        //Include delimiters and tab length in length values
        int i = 0;
        for (i = i; i < middleBoundaryOfFieldLabels; i++) {
            halfOfEntryFieldsDisplayLen += fieldLabels[i].length()
                    + COL_DELIMITER.length()
                    + fieldSpannedLengths[i];
        }

        END_BOUND_OF_MID_DELIMITER = halfOfEntryFieldsDisplayLen;

        int totalEntryDisplayLen = halfOfEntryFieldsDisplayLen;
        int lastFieldIdx = fieldLabels.length - 1;

        for (i = i; i < fieldLabels.length; i++) {
            totalEntryDisplayLen += fieldLabels[i].length()
                    + fieldSpannedLengths[i];

            if (lastFieldIdx != i)
                totalEntryDisplayLen += COL_DELIMITER.length();
        }

        TOTAL_LENGTH_OF_ENTRY_DISPLAYS = totalEntryDisplayLen;

        StringBuilder entryStrFormatBuilder = new StringBuilder();

        ENTRY_FORMAT = entryStrFormatBuilder
                .repeat(' ', tabLength)
                .append(fieldLabels[0])
                    .append("%-")
                    .append(fieldSpannedLengths[0])
                    .append('d')
                .append(COL_DELIMITER)
                .append(fieldLabels[1])
                    .append("%0")
                    .append(fieldSpannedLengths[1])
                    .append('d')
                .append(COL_DELIMITER)
                .append(fieldLabels[2])
                    .append("%-")
                    .append(fieldSpannedLengths[2])
                    .append('s')
                .append(COL_DELIMITER)
                .append(fieldLabels[3])
                    .append("%-")
                    .append(fieldSpannedLengths[3])
                    .append('s')
                .toString();
    }

    public static String getHeaderDisplay() {
        String entriesHeaderContentLeftPiece = "(--ENTRIES ";
        String entriesHeaderContentRightPiece = "FETCHED--)";

        //Trim off single trailing whitespace of delimiter (push bounds back one)
        int endBoundOfMidDelimiterPushedBackOne = END_BOUND_OF_MID_DELIMITER - 1;

        StringBuilder entriesHeaderBuilder = new StringBuilder();

        int entriesHeaderLeftPadLength = endBoundOfMidDelimiterPushedBackOne - entriesHeaderContentLeftPiece.length();
        buildUpHeaderPadding(entriesHeaderBuilder, entriesHeaderLeftPadLength);

        buildUpHeaderCenter(entriesHeaderBuilder, entriesHeaderContentLeftPiece, entriesHeaderContentRightPiece);

        int entriesHeaderRightPadLength = TOTAL_LENGTH_OF_ENTRY_DISPLAYS - entriesHeaderBuilder.length();
        buildUpHeaderPadding(entriesHeaderBuilder, entriesHeaderRightPadLength);

        return entriesHeaderBuilder.toString();
    }

    private static void buildUpHeaderPadding(StringBuilder entriesHeaderBuilder, int padToLength) {
        entriesHeaderBuilder.repeat(' ', padToLength);
    }

    private static void buildUpHeaderCenter(StringBuilder entriesHeaderBuilder, String leftContentPiece, String rightContentPiece) {
        entriesHeaderBuilder
                .append(leftContentPiece)
                .append(rightContentPiece);
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