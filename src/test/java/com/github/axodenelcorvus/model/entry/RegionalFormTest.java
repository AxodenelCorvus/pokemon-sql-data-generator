package com.github.axodenelcorvus.model.entry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RegionalFormTest {

    @Test
    void testRegionsLookupByString() {
        assertEquals(RegionalForm.PALDEA,
                RegionalForm.getRegionBy("paldea"));
        assertEquals(RegionalForm.ALOLA,
                RegionalForm.getRegionBy("alola"));
        assertEquals(RegionalForm.GALAR,
                RegionalForm.getRegionBy("galar"));
        assertEquals(RegionalForm.HISUI,
                RegionalForm.getRegionBy("hisui"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "PALDEA"})
    void testRegionReturnsNull(String value) {
        assertNull(RegionalForm.getRegionBy(value));
    }

}
