package com.github.axodenelcorvus.fetcher;

import java.util.Map;

public final class SingleAlternativeFormMapProvider {

    /**
     * Provides the necessary immutable Map to assist in building sprite record entries for Pokemon that have a single non-regional alternative
     * sprite stemming off thier base form. Not all Pokemon that have a single alternative sprite are included in possible maps per
     * generation as some are either too insignificant or not easily determined from PokeAPI.
     * @param generationScope The Pokemon game generation for scope of current generation command run of application.
     * @return The requested immutable Map of Pokemon IDs to the matching form descriptors. Duplicate shared descriptor values present, so mapping
     * is not one-to-one.
     */
    public static Map<Integer, String> from(int generationScope) {
        return switch(generationScope) {
                                     //Correlating name of each ID
            case (1) -> Map.of();
            case (2) -> Map.of();
            case (3) -> Map.of(
                    382, "Primal", //Kyogre
                    383, "Primal" //Groudon
            );
            case (4) -> Map.of(
                    492, "Sky", //Shaymin
                    487, "Origin", //Giratina
                    484, "Origin", //Palkia
                    483, "Origin" //Dialga
            );
            case (5) -> Map.of(
                    648, "Pirouette", //Meloetta
                    641, "Therian", //Tornadus
                    642, "Therian", //Thundurus
                    645, "Therian" //Landorus
            );
            case (6) -> Map.of(
                    720, "Unbound" //Hoopa
            );
            case (7) -> Map.of(
                    746, "School", //Wishiwashi
                    801, "Original Paint" //Magearna
            );
            case (8) -> Map.of(
                    892, "Rapid Strike", //Urshifu
                    877, "Hangry", //Morpeko
                    849, "Low Key", //Toxtricity
                    901, "Bloodmoon", //Ursaluna
                    905, "Therian", //Enamorus
                    875, "Noice" //Eiscue
            );
            case (9) -> Map.of(
                    964, "Hero" //Palafin
            );
            default -> throw new RuntimeException("Lookup for map of single alternative forms passed incorrect generation value: " + generationScope);
        };
    }

}