package com.github.axodenelcorvus;

import java.util.Map;
import java.util.Optional;
import java.util.function.IntPredicate;

public enum GameGenerationScope {
    GEN_1((dexId) -> dexId >= 138 && dexId <= 142),
    GEN_2((dexId) -> false),
    GEN_3((dexId) -> dexId >= 345 && dexId <= 348),
    GEN_4((dexId) -> dexId >= 408 && dexId <= 411),
    GEN_5((dexId) -> dexId >= 564 && dexId <= 567),
    GEN_6((dexId) -> dexId >= 696 && dexId <= 699),
    GEN_7((dexId) -> false),
    GEN_8((dexId) -> dexId >= 880 && dexId <= 883),
    GEN_9((dexId) -> false);

    private final IntPredicate fossilPokemonConfirmer;
    private Map<Integer, String> singleAlternativeFormsById = null;

    //Must be volatile so that reads are accurate when doing checks after flipped
    //should only be flipped at direct endpoint of delayedLoadSingleAltFormDescriptors
    private volatile boolean hasSingleAlternativeFormsByIdLoaded = false;

    GameGenerationScope(IntPredicate fossilPokemonConfirmer) {
        this.fossilPokemonConfirmer = fossilPokemonConfirmer;
    }

    /*
    Multiple threads running early in program at same time may create and assign same map repeatedly
    for the used generation without synchronizing. With synchronizing, the first batch/group of threads will need
    to wait until method is unlocked by whatever thread entered first.

    When the other threads enter the loading area and see the singleAlternativeFormsById map is
    already loaded they will go back to getSingleAlternativeFormDescriptor and fulfill the get request.
    Afterwards, the method won't be called again for future batches as flag hasSingleAlternativeFormsByIdLoaded
    has been raised.
    */
    private synchronized void delayedLoadSingleAltFormDescriptors() {
        if (hasSingleAlternativeFormsByIdLoaded)
            return;

        singleAlternativeFormsById = switch (this) {
            //Names correlating to pokemon ID key off to right in comments
            case GameGenerationScope.GEN_1 -> Map.of();
            case GameGenerationScope.GEN_2 -> Map.of();
            case GameGenerationScope.GEN_3 -> Map.of(
                    382, "Primal", //Kyogre
                    383, "Primal" //Groudon
            );
            case GameGenerationScope.GEN_4 -> Map.of(
                    492, "Sky", //Shaymin
                    487, "Origin", //Giratina
                    484, "Origin", //Palkia
                    483, "Origin" //Dialga
            );
            case GameGenerationScope.GEN_5 -> Map.of(
                    648, "Pirouette", //Meloetta
                    641, "Therian", //Tornadus
                    642, "Therian", //Thundurus
                    645, "Therian" //Landorus
            );
            case GameGenerationScope.GEN_6 -> Map.of(
                    720, "Unbound" //Hoopa
            );
            case GameGenerationScope.GEN_7 -> Map.of(
                    746, "School", //Wishiwashi
                    801, "Original Paint" //Magearna
            );
            case GameGenerationScope.GEN_8 -> Map.of(
                    892, "Rapid Strike", //Urshifu
                    877, "Hangry", //Morpeko
                    849, "Low Key", //Toxtricity
                    901, "Bloodmoon", //Ursaluna
                    905, "Therian", //Enamorus
                    875, "Noice" //Eiscue
            );
            case GameGenerationScope.GEN_9 -> Map.of(
                    964, "Hero" //Palafin
            );
        };

        hasSingleAlternativeFormsByIdLoaded = true;
    }

    public int asInt() {
        return this.ordinal() + 1;
    }

    public static Optional<GameGenerationScope> from(int generation) {
        GameGenerationScope[] generations = GameGenerationScope.values();
        int generationIdx = generation - 1;

        if (generationIdx < 0 || generationIdx > generations.length - 1)
            return Optional.empty();
        else
            return Optional.of(generations[generationIdx]);
    }

    public Optional<String> getSingleAlternativeFormDescriptor(int pokemonId) {
        if (!this.hasSingleAlternativeFormsByIdLoaded)
            this.delayedLoadSingleAltFormDescriptors();
        return Optional.ofNullable(singleAlternativeFormsById.get(pokemonId));
    }

    public boolean hasFossilPokemon(int pokemonId) {
        return fossilPokemonConfirmer.test(pokemonId);
    }

    public static String getOutOfRangeErrMsg() {
        return "Specified generation not in supported range %d to %d"
                    .formatted(GEN_1.ordinal() + 1, GEN_9.ordinal() + 1);
    }
}