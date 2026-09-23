package com.github.axodenelcorvus;

import com.github.axodenelcorvus.entry.SqlStringResolver;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SqlStringResolverTests {
    private final SqlStringResolver sqlStrResolver = new SqlStringResolver();

    @Test
    void testSingleQuotesResolution() {
        String fieldValueToResolve = "Farfetch'd";
        String expectedFieldFormatResult = "'Farfetch''d'";
        String actualFieldFormatResult =
                sqlStrResolver.apply(fieldValueToResolve);

        assertEquals(expectedFieldFormatResult, actualFieldFormatResult);
    }

    @Test
    void testSingleQuotesResolutionConsecutiveQuotes() {
        String fieldValueToResolve = "One''Two";
        String expectedFieldFormatResult = "'One''''Two'";
        String actualFieldFormatResult =
                sqlStrResolver.apply(fieldValueToResolve);

        assertEquals(expectedFieldFormatResult, actualFieldFormatResult);
    }

    @Test
    void testNullStringFieldResolution() {
        String fieldValueToResolve = null;
        String expectedFieldFormatAbsentSingleQuotes = "null";
        String actualFieldFormatResult =
                sqlStrResolver.apply(fieldValueToResolve);

        assertEquals(expectedFieldFormatAbsentSingleQuotes, actualFieldFormatResult);
    }
}
