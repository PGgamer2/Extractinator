package dev.alexnijjar.extractinator.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.recipes.IngredientCodec;
import com.teamresourceful.resourcefullib.common.codecs.tags.HolderSetCodec;
import com.teamresourceful.resourcefullib.common.recipe.CodecRecipe;
import dev.alexnijjar.extractinator.config.ExtractinatorConfig;
import dev.alexnijjar.extractinator.registry.ModRecipeSerializers;
import dev.alexnijjar.extractinator.registry.ModRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public record ExtractinatorRecipe(ResourceLocation id, Ingredient input,
                                  List<Drop> outputs) implements CodecRecipe<Container> {

    public static Codec<ExtractinatorRecipe> codec(ResourceLocation id) {
        return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                IngredientCodec.CODEC.fieldOf("input").forGetter(ExtractinatorRecipe::input),
                Drop.CODEC.listOf().fieldOf("drops").forGetter(ExtractinatorRecipe::outputs)
        ).apply(instance, ExtractinatorRecipe::new));
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public @NotNull ResourceLocation id() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.EXTRACTINATOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.EXTRACTINATOR_RECIPE.get();
    }

    public List<Ingredient> getOutputs() {
        return this.outputs().stream().map(d -> d.drops.stream().map(Holder::value).map(i -> new ItemStack(i, (int) (d.maxDropCount() * ExtractinatorConfig.lootMultiplier))).map(Ingredient::of).toList()).flatMap(List::stream).toList();
    }

    public static ExtractinatorRecipe findFirst(Level level, Predicate<ExtractinatorRecipe> filter) {
        return getRecipes(level).stream().filter(filter).findFirst().orElse(null);
    }

    public static List<ExtractinatorRecipe> getRecipes(Level level) {
        return level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.EXTRACTINATOR_RECIPE.get());
    }

    public record Drop(HolderSet<Item> drops, double dropChance, int minDropCount, int maxDropCount) {
        public static final Codec<Drop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                HolderSetCodec.of(Registry.ITEM).fieldOf("drop").forGetter(Drop::drops),
                Codec.DOUBLE.fieldOf("drop_chance").orElse(1.0).forGetter(Drop::dropChance),
                Codec.INT.fieldOf("min_drop_count").orElse(1).forGetter(Drop::minDropCount),
                Codec.INT.fieldOf("max_drop_count").orElse(1).forGetter(Drop::maxDropCount)
        ).apply(instance, Drop::new));
    }
}