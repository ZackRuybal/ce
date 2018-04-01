package com.taiter.ce.Enchantments.Tool;

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



import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.taiter.ce.Enchantments.CEnchantment;
import org.bukkit.inventory.PlayerInventory;

import java.util.Random;


public class Smelting extends CEnchantment {

	public Smelting(EnchantmentTarget app) {
		super(app);		
		triggers.add(Trigger.BLOCK_BROKEN);
		this.resetMaxLevel();
	}

	@Override
	public void effect(Event e, ItemStack item, int level) {
		BlockBreakEvent event = (BlockBreakEvent) e;
		Player player = event.getPlayer();
						
		if(!event.getBlock().getDrops(item).isEmpty()) {
		
			ItemStack itemToDrop = null;
			Material drop = null;
			short dur = 0;
			
			Block b = event.getBlock();
			
			Material m = b.getType();
			
			if(m == Material.STONE)
				drop = m;
			else if(m == Material.COBBLESTONE)
				drop = Material.STONE;
			else if(m == Material.IRON_ORE)
				drop = Material.IRON_INGOT;
			else if(m == Material.GOLD_ORE)
				drop = Material.GOLD_INGOT;
			else if(m.toString().contains("LOG")) {
				drop = Material.COAL;
				dur = 1;
			}
			else if(m == Material.SAND)
				drop = Material.GLASS;
			else if(m == Material.CLAY)
				drop = Material.BRICK;
			
			if(drop != null) {
				PlayerInventory inventory = player.getInventory();
				ItemStack mainHand = inventory.getItemInMainHand();
				switch (drop){
					case IRON_INGOT:
					case GOLD_INGOT:
						b.setType(Material.DIAMOND_ORE);
						int quantity = quantityDroppedWithBonus(mainHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS),new Random());
						itemToDrop = new ItemStack(drop, quantity);
						itemToDrop.setDurability(dur);
						break;
					default:
						itemToDrop = new ItemStack(drop, event.getBlock().getDrops(mainHand).size()); //Prevents unallowed tool usage (Wooden Pickaxe -> Diamond Ore)
						itemToDrop.setDurability(dur);
						break;
				}
				event.setCancelled(true);
				player.getWorld().dropItem(b.getLocation(), itemToDrop); //dropItemNaturally() has a random offset, which is not good :(
				player.getWorld().playEffect(b.getLocation(), Effect.MOBSPAWNER_FLAMES, 12);
				b.setType(Material.AIR);
				// Fix the no-durability-usage
				boolean damageItem = false;
				if(player.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.DURABILITY)){
					int chance = (int)Math.round((Math.random()*100));
					int unbreaking_level = player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DURABILITY);
					int percentage = (int)Math.round((100/(unbreaking_level+1)));
					if (chance<percentage) damageItem = true;
				}
				else{damageItem = true;}
				if(!player.getGameMode().equals(GameMode.CREATIVE) && damageItem){
					//They messed this up
					if((player.getInventory().getItemInMainHand().getDurability()+1) > player.getInventory().getItemInMainHand().getType().getMaxDurability()){
						// And the Spigot team messed this up
						//player.getInventory().removeItem(player.getInventory().getItemInMainHand());
						// Doesnt reliably pick the correct item from the inventory if you have multiple similar ones
						player.getInventory().getItemInMainHand().setAmount(0);
						// hope this doesn't cause issues
					} else {
						player.getInventory().getItemInMainHand().setDurability((short) (player.getInventory().getItemInMainHand().getDurability()+1));
					}
				}
			}
			
		}
		
	}
	
	@Override
	public void initConfigEntries() {
	}

	//custom version
	public int quantityDroppedWithBonus(int fortune, Random random)
	{
		if (fortune > 0)
		{
			int i = random.nextInt(fortune + 2) - 1;
			if (i < 0)
			{
				i = 0;
			}

			return i + 1;
		}
		else
		{
			return 1;
		}
	}
}
