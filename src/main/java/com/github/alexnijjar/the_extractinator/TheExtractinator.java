package com.github.alexnijjar.the_extractinator;

import com.github.alexnijjar.the_extractinator.compat.rei.LootTable;
import com.github.alexnijjar.the_extractinator.config.TEConfig;
import com.github.alexnijjar.the_extractinator.loot.ExtractinatorLootTables;
import com.github.alexnijjar.the_extractinator.loot.LootTableModifier;
import com.github.alexnijjar.the_extractinator.registry.*;
import com.github.alexnijjar.the_extractinator.structure.ConfiguredStructures;
import com.github.alexnijjar.the_extractinator.util.TEIdentifier;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.loot.LootManager;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TheExtractinator implements ModInitializer {

    public static final String MOD_ID = "the_extractinator";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Identifier REI_DISPLAY_LOOT_PACKET_ID = new TEIdentifier("rei_display_loot");
    public static TEConfig CONFIG;

    @Override
    public void onInitialize() {

        // Config.
        AutoConfig.register(TEConfig.class, Toml4jConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(TEConfig.class).getConfig();

        // Registry.
        TEBlocks.register();
        TEBlockEntities.register();
        TEItems.register();
        TEOres.register();
        ConfiguredStructures.register();
        TEStructures.register();
        TELootTables.register();
        LootTableModifier.modifyCabinLoot();
        TERecipes.register();
        TEStats.register();

        // REI
        // Obtains all the loot tables from "gameplay/extractinator" and sends it to players entering the server.
        // The loot is then processed and displayed in REI.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, minecraftServer) -> {

            LootManager manager = minecraftServer.getLootManager();
            List<LootTable> tables = ExtractinatorLootTables.getLoot(manager);

            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeCollection(tables, (b, t) -> {
                b.writeEnumConstant(t.tier);

                b.writeCollection(t.slots, (b2, s) -> {
                    b2.writeIdentifier(s.item);
                    b2.writeEnumConstant(s.rarity);

                    b2.writeIntArray(new int[]{s.range.getMinimum(), s.range.getMaximum()});
                });

                b.writeString(t.namespace);
            });

            sender.sendPacket(REI_DISPLAY_LOOT_PACKET_ID, buf);

            if (minecraftServer.isDedicated())
                TheExtractinator.LOGGER.info("Sent REI Loot info to " + handler.player.getDisplayName().asString());
        });

        LOGGER.info("The Extractinator initialized!");
    }
}