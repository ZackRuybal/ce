package com.taiter.ce.Enchantments.Bow;

import com.taiter.ce.CEListener;
import org.bukkit.GameMode;

/*
* This file is written by _fantasm0_ for the CustomEnchants plugin.
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

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.taiter.ce.Enchantments.CEnchantment;

public class Volley extends CEnchantment {

    // Constants
    private static final int CONE_DEGREES = 45; // The volley will spawn a cone of CONE_DEGREES in front of the player.

    public Volley(EnchantmentTarget app) {
        super(app);
        triggers.add(Trigger.SHOOT_BOW); // Shooting a bow triggers a volley (spawning extra arrows)
    }

    @Override
    public void effect(Event e, ItemStack item, int level) {
        if (e instanceof EntityShootBowEvent) {
            //Spawn arrows with a slightly altered direction, based on the location of the first arrow.
            volley((EntityShootBowEvent) e, item, level);
        }
    }

    // Helper method that spawns the volley.
    private void volley(EntityShootBowEvent e, ItemStack item, int lvl) {
        Player p = (Player) e.getEntity();
        int amount = 1 + 2 * lvl; // Keep amount of arrows uneven, 2 extra arrows in a volley per level.
        ItemStack bow = e.getBow();

        Arrow oldArrow = (Arrow) e.getProjectile();
        int fireTicks = oldArrow.getFireTicks();
        int knockbackStrength = oldArrow.getKnockbackStrength();
        boolean critical = oldArrow.isCritical();
        String metadata = oldArrow.getMetadata("ce.bow.enchantment").get(0).asString();

        double angleBetweenArrows = (CONE_DEGREES / (amount - 1)) * Math.PI / 180;
        double pitch = (p.getLocation().getPitch() + 90) * Math.PI / 180;
        double yaw = (p.getLocation().getYaw() + 90 - CONE_DEGREES / 2) * Math.PI / 180;

        // Starting direction values for the cone, each arrow increments it's direction on these values.
        double sZ = Math.cos(pitch);

        for (int i = 0; i < amount; i++) { // spawn all arrows in a cone of 90 degrees (equally distributed).;
            double nX = Math.sin(pitch) * Math.cos(yaw + angleBetweenArrows * i);
            double nY = Math.sin(pitch) * Math.sin(yaw + angleBetweenArrows * i);
            Vector newDir = new Vector(nX, sZ, nY);

            Arrow arrow = p.launchProjectile(Arrow.class);
            arrow.setShooter(p);
            arrow.setVelocity(newDir.normalize().multiply(oldArrow.getVelocity().length())); // Need to make sure arrow has same speed as original arrow.
            arrow.setFireTicks(fireTicks); // Set the new arrows on fire if the original one was 
            arrow.setKnockbackStrength(knockbackStrength);
            arrow.setCritical(critical);

            if (i == lvl && p.getGameMode() != GameMode.CREATIVE && bow != null && bow.getType() != Material.AIR && bow.getEnchantmentLevel(Enchantment.ARROW_INFINITE) <= 0) {
                arrow.setPickupStatus(oldArrow.getPickupStatus());
            } else {
                arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
            }
            CEListener.projectiles.add(arrow.getUniqueId());
            arrow.setMetadata("ce.bow.enchantment", new FixedMetadataValue(getPlugin(), metadata));
        }
        oldArrow.remove(); // Remove original arrow.
    }

    @Override
    public void initConfigEntries() {
    }
}
