/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.maze.kit;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 *
 * @author m1v3rpwn
 */
public abstract class Kit {

    protected ItemStack boots, legs, chest, helm, sword;
    protected ItemStack[] inv;
    protected PotionEffect[] effects;
    protected String name;
    protected double health;

    public ItemStack getBoots() {
        return boots;
    }

    public ItemStack getLegs() {
        return legs;
    }

    public ItemStack getChest() {
        return chest;
    }

    public ItemStack getHelm() {
        return helm;
    }

    public ItemStack getSword() {
        return sword;
    }

    public ItemStack[] getInv() {
        return inv;
    }

    public String getName() {
        return name;
    }

    public double getHealth() {
        return health;
    }

    public void apply(LivingEntity p) {
        p.getEquipment().setBoots(boots);
        p.getEquipment().setLeggings(legs);
        p.getEquipment().setChestplate(chest);
        p.getEquipment().setHelmet(helm);
        p.getActivePotionEffects().clear();
        for (PotionEffect pe : p.getActivePotionEffects()) {
            p.addPotionEffect(new PotionEffect(pe.getType(), 1, 1), true);
        }
        if (p instanceof Player) {
            Player pl = (Player) p;
            pl.getInventory().clear();
            pl.getInventory().setItem(0, sword);
            pl.getInventory().addItem(inv);
            pl.setHealthScale(health);
            pl.setFoodLevel(20);
            pl.setSaturation(2000f);
            p.setMaxHealth(health);
            p.setHealth(health);
            for (PotionEffect pe : effects) {
                p.addPotionEffect(pe, true);
            }
        } else {
            p.getEquipment().setItemInHand(sword);
            p.setMaxHealth(health / 2);
            p.setHealth(health / 2);
            if (p.getEquipment().getHelmet() == null) {
                p.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
            }
        }

    }
}
