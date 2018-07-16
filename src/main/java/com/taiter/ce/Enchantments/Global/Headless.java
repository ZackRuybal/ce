package com.taiter.ce.Enchantments.Global;

import com.taiter.ce.EffectManager;
import com.taiter.ce.Enchantments.CEnchantment;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Headless extends CEnchantment {

    public Headless(EnchantmentTarget app) {
        super(app);
        triggers.add(Trigger.DAMAGE_GIVEN);
        resetMaxLevel();
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
        final Player player = (Player) event.getDamager();
        final LivingEntity ent = (LivingEntity) event.getEntity();

        new BukkitRunnable() {
            @Override
            public void run() {

                if (ent.getHealth() <= 0) {
                    ItemStack skull = null;
                    if (ent instanceof Player) {
                        skull = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta sm = (SkullMeta) skull.getItemMeta();
                        sm.setOwningPlayer((OfflinePlayer) ent);
                        skull.setItemMeta(sm);
                    } else if (ent instanceof WitherSkeleton) {
                        skull = new ItemStack(Material.WITHER_SKELETON_SKULL);
                    } else if (ent instanceof Skeleton) {
                        skull = new ItemStack(Material.SKELETON_SKULL);
                    } else if (ent instanceof Zombie) {
                        skull = new ItemStack(Material.ZOMBIE_HEAD);
                    } else if (ent instanceof Creeper) {
                        skull = new ItemStack(Material.CREEPER_HEAD);
                    }
                    if (skull != null) {
                        ent.getWorld().dropItem(ent.getLocation(), skull);
                        EffectManager.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.1f, 1.5f);
                    }
                }
            }
        }.runTaskLater(getPlugin(), 5l);

    }

    @Override
    public void initConfigEntries() {
    }
}
