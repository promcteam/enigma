package com.promcteam.enigma;

import com.promcteam.enigma.cfg.Cfg;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import lombok.Getter;
import me.travja.darkrise.core.legacy.util.item.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Enigma extends JavaPlugin {
    @Getter
    private static       Enigma                   instance;
    @Getter
    private static final Map<String, WorldChests> worlds = new HashMap<>(3);

    public static void addWorld(final WorldChests world) {
        worlds.put(world.getWorld().getName(), world);
    }

    public static WorldChests getWorld(final String world) {
        return worlds.get(world);
    }

    {
        Enigma.instance = this;
        ConfigurationSerialization.registerClass(EnchantmentStorageBuilder.class, "Enigma_EnchantmentStorageMeta");
        ConfigurationSerialization.registerClass(FireworkEffectBuilder.class, "Enigma_FireworkEffectMeta");
        ConfigurationSerialization.registerClass(LeatherArmorBuilder.class, "Enigma_LeatherArmorMeta");
        ConfigurationSerialization.registerClass(PotionDataBuilder.class, "Enigma_PotionMeta");
        ConfigurationSerialization.registerClass(FireworkBuilder.class, "Enigma_FireworkMeta");
        ConfigurationSerialization.registerClass(BookDataBuilder.class, "Enigma_BookMeta");
        ConfigurationSerialization.registerClass(SkullBuilder.class, "Enigma_SkullMeta");
        ConfigurationSerialization.registerClass(MapBuilder.class, "Enigma_MapMeta");
        ConfigurationSerialization.registerClass(ItemBuilder.class, "Enigma_Item");

        ConfigurationSerialization.registerClass(WorldChests.class, "Enigma_WorldChests");
        ConfigurationSerialization.registerClass(MapLocation.class, "Enigma_MapLocation");

        ConfigurationSerialization.registerClass(ItemCommand.class, "Enigma_ItemCommand");
    }

    @Override
    public void onEnable() {
        Cfg.init();
        WorldListener.init();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, Cfg::save, 1, Cfg.getAutoSaveTime());
        runTask(() -> worlds.values().forEach(WorldChests::addMissingChests));
    }

    public static boolean isInRegion(final Location location) {
        WorldGuardPlatform platform = WorldGuard.getInstance().getPlatform(); // Commented are <1.13 api things
        RegionManager manager = platform.getRegionContainer()// WorldGuardPlugin.inst().getRegionContainer()
                .get(BukkitAdapter.adapt(location.getWorld()));

        return manager != null && manager//.getApplicableRegions(location).size() > 0;
                .getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ())).size() > 0;
    }

    @Override
    public void onDisable() {
        Cfg.save();
    }

    public static void runTaskLater(final Runnable runnable, final int delay) {
        Bukkit.getScheduler().runTaskLater(instance, runnable, delay);
    }

    public static void runTask(final Runnable runnable) {
        Bukkit.getScheduler().runTask(instance, runnable);
    }

    public static void async(final Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, runnable);
    }
}