package com.taiter.ce.Enchantments.Global;

/*
* This file is part of Custom Enchantments
* Copyright (C) Taiterio 2015
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published by the
* Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
* for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

import com.taiter.ce.EffectManager;
import com.taiter.ce.Enchantments.CEnchantment;
import com.taiter.ce.Tools;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IceAspect extends CEnchantment {

    int SlowStrength;
    int SlowDuration;
    int chanceFreeze;
    int SpecialFreezeDuration;
    int chanceSpecialFreeze;
    boolean specialFreeze;

    public List<HashMap<String, HashMap>> IceLists = new ArrayList<>();

    public IceAspect(EnchantmentTarget app) {
        super(app);
        configEntries.put("SlowStrength", 5);
        configEntries.put("SlowDuration", 40);
        configEntries.put("ChanceFreeze", 60);
        configEntries.put("SpecialFreeze", true);
        configEntries.put("SpecialFreezeDuration", 60);
        configEntries.put("ChanceSpecialFreeze", 10);
        triggers.add(Trigger.DAMAGE_GIVEN);
        triggers.add(Trigger.SHOOT_BOW);
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
        int i = Tools.random.nextInt(100);

        if (i < chanceFreeze) {
            Tools.addPotionEffect(((LivingEntity) event.getEntity()), new PotionEffect(PotionEffectType.SLOW, SlowDuration, SlowStrength));
            EffectManager.playSound(event.getEntity().getLocation(), Sound.BLOCK_SNOW_BREAK, 0.6f, 2f);
        }
        if (specialFreeze) {
            if (i < chanceSpecialFreeze) {
                if (event.getEntity() instanceof LivingEntity) {
                    LivingEntity ent = (LivingEntity) event.getEntity();
                    Player p = null;

                    if (event.getDamager() instanceof Player)
                        p = (Player) event.getDamager();
                    else
                        p = (Player) ((Projectile) event.getDamager()).getShooter();

                    Tools.addPotionEffect(ent, new PotionEffect(PotionEffectType.SLOW, SpecialFreezeDuration + 20, 10));
                    final HashMap<String, HashMap> list = getIgloo(ent.getLocation(), 3, p);

                    generateCooldown(p, getOriginalName(), SpecialFreezeDuration);

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            deleteIce(list);
                            IceLists.remove(list);
                        }
                    }.runTaskLater(getPlugin(), SpecialFreezeDuration);
                }
            }
        }
    }

    public void deleteIce(HashMap<String, HashMap> list) {
        for (Object key : list.get("material").keySet()) {
            Block block = ((Location) key).getBlock();
            block.setType((Material) list.get("material").get(key));
            block.setBlockData((BlockData) list.get("blockdata").get(key), false);
            block.removeMetadata("ce.Ice", getPlugin());
        }
        IceLists.remove(list);
    }

    private HashMap<String, HashMap> getIgloo(Location start, int size, Player p) {
        HashMap<String, HashMap> list = new HashMap<>();
        list.put("material", new HashMap<Location, Material>());
        list.put("blockdata", new HashMap<Location, BlockData>());
        int bx = start.getBlockX();
        int by = start.getBlockY();
        int bz = start.getBlockZ();

        for (int x = bx - size; x <= bx + size; x++)
            for (int y = by - 1; y <= by + size; y++)
                for (int z = bz - size; z <= bz + size; z++) {
                    double distancesquared = (bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y));
                    if (distancesquared < (size * size) && distancesquared >= ((size - 1) * (size - 1))) {
                        org.bukkit.block.Block b = new Location(start.getWorld(), x, y, z).getBlock();
                        if ((b.getType() == Material.AIR || (!b.getType().equals(Material.CARROT) && !b.getType().equals(Material.POTATO) && !b.getType().equals(Material.WHEAT)
                                && !b.getType().toString().contains("SIGN") && !b.getType().isSolid())) && Tools.checkWorldGuard(b.getLocation(), p, "PVP", false)) {
                            list.get("material").put(b.getLocation(), b.getType());
                            list.get("blockdata").put(b.getLocation(), b.getBlockData());
                            b.setType(Material.ICE);
                            b.setMetadata("ce.Ice", new FixedMetadataValue(getPlugin(), null));
                        }
                    }
                }
        return list;
    }

    @Override
    public void initConfigEntries() {
        SlowStrength = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".SlowStrength"));
        SlowDuration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".SlowDuration"));
        chanceFreeze = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".ChanceFreeze"));
        SpecialFreezeDuration = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".SpecialFreezeDuration"));
        chanceSpecialFreeze = Integer.parseInt(getConfig().getString("Enchantments." + getOriginalName() + ".ChanceSpecialFreeze"));
        specialFreeze = Boolean.parseBoolean(getConfig().getString("Enchantments." + getOriginalName() + ".SpecialFreeze"));
    }
}
