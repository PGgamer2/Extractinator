package dev.alexnijjar.extractinator.registry;

import dev.alexnijjar.extractinator.config.ExtractinatorConfig;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Arrays;
import java.util.function.Supplier;

public class ModOres {
    private static ConfiguredFeature<?, ?> createSiltConfigured() {
        return new ConfiguredFeature<>
                (Feature.ORE, new OreConfiguration(
                        OreFeatures.NATURAL_STONE,
                        ModBlocks.SILT.get().defaultBlockState(),
                        ExtractinatorConfig.siltVeinSize));
    }

    private static PlacedFeature createSilt() {
        return new PlacedFeature(
                Holder.direct(createSiltConfigured()),
                Arrays.asList(
                        CountPlacement.of(ExtractinatorConfig.siltVeinsPerChunk),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(ExtractinatorConfig.siltMinHeight), VerticalAnchor.absolute(ExtractinatorConfig.siltMaxHeight))
                ));
    }

    private static ConfiguredFeature<?, ?> createSlushConfigured() {
        return new ConfiguredFeature<>
                (Feature.ORE, new OreConfiguration(
                        OreFeatures.STONE_ORE_REPLACEABLES,
                        ModBlocks.SLUSH.get().defaultBlockState(),
                        ExtractinatorConfig.slushVeinSize));
    }

    private static PlacedFeature createSlush() {
        return new PlacedFeature(
                Holder.direct(createSlushConfigured()),
                Arrays.asList(
                        CountPlacement.of(ExtractinatorConfig.slushVeinsPerChunk),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(ExtractinatorConfig.slushMinHeight), VerticalAnchor.absolute(ExtractinatorConfig.slushMaxHeight))
                ));
    }

    public static final Supplier<PlacedFeature> SILT_ORE = register("silt_ore", ModOres::createSilt);
    public static final Supplier<PlacedFeature> SLUSH_ORE = register("slush_ore", ModOres::createSlush);
    public static final Supplier<ConfiguredFeature<?, ?>> SILT_CONFIGURED_ORE = registerConfigured("silt_ore", ModOres::createSiltConfigured);
    public static final Supplier<ConfiguredFeature<?, ?>> SLUSH_CONFIGURED_ORE = registerConfigured("slush_ore", ModOres::createSlushConfigured);

    private static <T extends PlacedFeature> Supplier<T> register(String id, Supplier<T> object) {
        return RegistryHelpers.register(BuiltinRegistries.PLACED_FEATURE, id, object);
    }

    private static <T extends ConfiguredFeature<?, ?>> Supplier<T> registerConfigured(String id, Supplier<T> object) {
        return RegistryHelpers.register(BuiltinRegistries.CONFIGURED_FEATURE, id, object);
    }

    public static void init() {
    }
}
