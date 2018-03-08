package com.taiter.ce.CItems;

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


import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;



public class Powergloves extends CItem {

	/* 投擲速度倍率 */
	int	ThrowSpeedMultiplier;
	/* 舉起後多久可以投擲 */
	int	ThrowDelayAfterGrab;
	/* 最大舉起時間 */
	int MaxGrabTicks;
    public static String POWERGLOVES_META_KEY = "ce.Powergloves";

	public Powergloves(String originalName, ChatColor color, String lDescription, long lCooldown, Material mat) {
		super(originalName, color, lDescription, lCooldown, mat);
		this.configEntries.put("ThrowSpeedMultiplier", 60);
		this.configEntries.put("ThrowDelayAfterGrab", 20);
		this.configEntries.put("MaxGrabTicks", 200);
		triggers.add(Trigger.INTERACT_RIGHT);
		triggers.add(Trigger.INTERACT_ENTITY);
	}

    @Override
    public boolean effect(Event event, final Player player) {

        if (event instanceof PlayerInteractEntityEvent) {
            PlayerInteractEntityEvent e = (PlayerInteractEntityEvent) event;
            e.setCancelled(true);
            final Entity clicked = e.getRightClicked();

            /* 假如玩家沒有Powergloves的Metadata */
            if (!hasCooldown(player, POWERGLOVES_META_KEY)) {

                /* 假如目標 [ 是生物實體 & 不是死的 & 自己不是騎乘者 & 沒有騎乘者 ] */
                if (clicked instanceof LivingEntity && !clicked.isDead() && !clicked.getPassengers().contains(player) && player.getPassengers().isEmpty() && player.addPassenger(clicked)) {
                    player.getWorld().playEffect(player.getLocation(), Effect.ZOMBIE_CHEW_IRON_DOOR, 10);

                    new BukkitRunnable() {

                        int GrabTime = 0;
                        //ItemStack	current		= player.getInventory().getItemInMainHand();

                        @Override
                        public void run() {
                            GrabTime++;
                            if (player.isOnline() && !player.isDead() && player.getPassengers().contains(clicked)) {
                                if (GrabTime == ThrowDelayAfterGrab) {
                                    player.getWorld().playEffect(player.getLocation(), Effect.CLICK2, 10);
                                    generateCooldown(player, POWERGLOVES_META_KEY, MaxGrabTicks);
                                    if (clicked.getCustomName() == null) {
                                        player.sendMessage("You catched " + clicked.getName() + "! Right click to throw!");
                                    } else {
                                        player.sendMessage("You catched " + clicked.getCustomName() + "! Right click to throw!");
                                    }
                                }
                                if (GrabTime >= MaxGrabTicks) {
                                    this.cancel();
                                    player.getWorld().playEffect(player.getLocation(), Effect.CLICK1, 10);
                                    generateCooldown(player, getOriginalName(), getCooldown(), true);
                                    if (clicked.isValid()) {
                                        if (clicked.getCustomName() == null) {
                                            player.sendMessage("§4Oh! The §f" + clicked.getName() + " §4has run off!");
                                        } else {
                                            player.sendMessage("§4Oh! The §f" + clicked.getCustomName() + " §4has run off!");
                                        }
                                        clicked.leaveVehicle();
                                    }
                                }
                            } else {
                                this.cancel();
                                if (!hasCooldown(player, getOriginalName())) {
                                    generateCooldown(player, getOriginalName(), getCooldown(), true);
                                    generateCooldown(player, POWERGLOVES_META_KEY, 0);
                                }
                            }
                        }
                    }.runTaskTimer(main, 0L, 1L);
                }
            }
        } else if (event instanceof PlayerInteractEvent) {
            if (hasCooldown(player, POWERGLOVES_META_KEY) && !player.getPassengers().isEmpty()) {
                generateCooldown(player, getOriginalName(), getCooldown(), true);
                generateCooldown(player, POWERGLOVES_META_KEY, 0);
                for (Entity passenger : player.getPassengers()) {
                    passenger.leaveVehicle();
                    passenger.setVelocity(player.getLocation().getDirection().multiply(ThrowSpeedMultiplier));
                    player.getWorld().playEffect(player.getLocation(), Effect.ZOMBIE_DESTROY_DOOR, 10);
                }
                return true;
            }
        }

        return false;
    }

	@Override
	public void initConfigEntries() {
		ThrowDelayAfterGrab = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".ThrowDelayAfterGrab"));
        MaxGrabTicks = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".MaxGrabTicks"));
		ThrowSpeedMultiplier = Integer.parseInt(getConfig().getString("Items." + getOriginalName() + ".ThrowSpeedMultiplier"));
	}

}
