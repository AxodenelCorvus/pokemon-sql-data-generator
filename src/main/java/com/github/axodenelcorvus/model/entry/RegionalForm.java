package com.github.axodenelcorvus.model.entry;


/**
 * <p>Represents the regional forms of Pokemon possible. Some species have
 * multiple regional forms stemming from their base form.</p>
 *
 * <p>A Pokemon should have a STANDARD regional form match for their base form,
 * which is one their species was first introduced with.</p>
 *
 * <p>Ordinal 0, the first constant STANDARD, is promised to remain in its position
 * as representing a Pokemon whose form orginates from their standard region (their standard form)</p>
 */
public enum RegionalForm {
    STANDARD(""),
    GALAR("galar"),
    HISUI("hisui"),
    ALOLA("alola"),
    PALDEA("paldea");

    private static final RegionalForm[] nonStandardForms;
    private static final String[] regionNames;

    static {
        RegionalForm[] regionalFormValues = RegionalForm.values();
        int numRegionalFormVals = regionalFormValues.length;

        nonStandardForms = new RegionalForm[numRegionalFormVals - 1];
        regionNames = new String[numRegionalFormVals - 1];


        //Copy each value (constant + region name) from ordinal positions 1 and on (skips STANDARD)
        //to previous position one behind in nonStandardForms and regionNames
        //Example of loop:
        //     1234
        //    0123
        //    0123
        for (int i = 1; i < regionalFormValues.length; i++) {
            nonStandardForms[i - 1] = regionalFormValues[i];
            regionNames[i - 1] = regionalFormValues[i].getRegionName();
        }
    }

    public static RegionalForm getRegionBy(String lowercaseRegionName) {
        for (int i = 0; i < regionNames.length; i++) {
            if (regionNames[i].equals(lowercaseRegionName))
                return nonStandardForms[i];
        }

        return null;
    }

    private final String regionName;

    RegionalForm(String regionName) {
        this.regionName = regionName;
    }

    public String getRegionName() {
        return regionName;
    }

}
