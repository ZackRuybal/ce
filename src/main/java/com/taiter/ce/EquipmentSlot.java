package com.taiter.ce;

import com.taiter.ce.Enchantments.CEnchantment;
import org.bukkit.enchantments.EnchantmentTarget;

public enum EquipmentSlot {
    HEAD {
        @Override
        public boolean isValid(CEnchantment ce) {
            return ce.getEnchantmentTarget() == EnchantmentTarget.ARMOR || ce.getEnchantmentTarget() == EnchantmentTarget.ARMOR_HEAD;
        }
    },
    CHEST {
        @Override
        public boolean isValid(CEnchantment ce) {
            return ce.getEnchantmentTarget() == EnchantmentTarget.ARMOR || ce.getEnchantmentTarget() == EnchantmentTarget.ARMOR_TORSO;
        }
    },
    LEGS {
        @Override
        public boolean isValid(CEnchantment ce) {
            return ce.getEnchantmentTarget() == EnchantmentTarget.ARMOR || ce.getEnchantmentTarget() == EnchantmentTarget.ARMOR_LEGS;
        }
    },
    FEET {
        @Override
        public boolean isValid(CEnchantment ce) {
            return ce.getEnchantmentTarget() == EnchantmentTarget.ARMOR || ce.getEnchantmentTarget() == EnchantmentTarget.ARMOR_FEET;
        }
    },
    MAIN_HAND {
        @Override
        public boolean isValid(CEnchantment ce) {
            return ce.getEnchantmentTarget() != EnchantmentTarget.ARMOR && ce.getEnchantmentTarget() != EnchantmentTarget.ARMOR_HEAD &&
                    ce.getEnchantmentTarget() != EnchantmentTarget.ARMOR_TORSO && ce.getEnchantmentTarget() != EnchantmentTarget.ARMOR_LEGS &&
                    ce.getEnchantmentTarget() != EnchantmentTarget.ARMOR_FEET;
        }

    };

    public abstract boolean isValid(CEnchantment ce);
}
