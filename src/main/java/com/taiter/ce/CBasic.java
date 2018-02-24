package com.taiter.ce;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public abstract class CBasic {

    static public enum Trigger {
        INTERACT,
        INTERACT_ENTITY,
        INTERACT_LEFT,
        INTERACT_RIGHT,
        MOVE,
        DAMAGE_GIVEN,
        DAMAGE_TAKEN,
        DAMAGE_NATURE, //Falldamage, damage by cactus, etc.
        BLOCK_PLACED,
        BLOCK_BROKEN,
        SHOOT_BOW,
        PROJECTILE_THROWN,
        PROJECTILE_HIT,
        WEAR_ITEM,
        DEATH
    }

    protected Plugin main = Main.plugin;

    protected static Map<String,Long> cooldown = new HashMap<>();
    protected HashSet<UUID> lockList = new HashSet<>();
    protected HashSet<Trigger> triggers = new HashSet<Trigger>();

    protected String displayName;
    protected String originalName;
    protected String permissionName;
    protected String typeString;

    protected HashMap<PotionEffectType, Integer> potionsOnWear = new HashMap<PotionEffectType, Integer>();

    protected Map<String, Object> configEntries = new LinkedHashMap<String, Object>();

    public Plugin getPlugin() {
        return this.main;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getOriginalName() {
        return this.originalName;
    }
    
    public String getPermissionName() {
        return this.permissionName;
    }

    public HashSet<Trigger> getTriggers() {
        return this.triggers;
    }

    public FileConfiguration getConfig() {
        return Main.config;
    }

    public String getType() {
        return this.typeString;
    }

    public HashMap<PotionEffectType, Integer> getPotionEffectsOnWear() {
        return this.potionsOnWear;
    }

    public boolean hasCooldown(Player p) {
        return hasCooldown(p, getOriginalName());
    }

    public static boolean hasCooldown(Player p, String name) {
        if (cooldown.isEmpty()) {
            return false;
        }
        String key = p.getUniqueId().toString() + name;
        if (cooldown.containsKey(key) && cooldown.get(key) > System.currentTimeMillis()) {
            return true;
        } else {
            cooldown.remove(key);
            return false;
        }
    }

    public static void generateCooldown(final Player p, String name, long ticks) {
        String key = p.getUniqueId().toString() + name;
        if (ticks > 0) {
            cooldown.put(key, System.currentTimeMillis() + (ticks * 50));
        } else {
            cooldown.remove(key);
        }
    }
}
