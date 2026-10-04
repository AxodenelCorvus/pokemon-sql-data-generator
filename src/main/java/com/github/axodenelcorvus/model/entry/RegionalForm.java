package com.github.axodenelcorvus.model.entry;

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
        int nonStandardFormsSize = regionalFormValues.length - 1;

        nonStandardForms = new RegionalForm[nonStandardFormsSize];
        regionNames = new String[nonStandardFormsSize];

        for (int i = 0; i < nonStandardFormsSize; i++) {
            nonStandardForms[i] = regionalFormValues[i + 1];
            regionNames[i] = regionalFormValues[i + 1].getRegionName();
        }
    }

    public static RegionalForm getRegionBy(String regionName) {
        for (int i = 0; i < regionNames.length; i++) {
            if (regionNames[i].equals(regionName)) {
                return nonStandardForms[i];
            }
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
