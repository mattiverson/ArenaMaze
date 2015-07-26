/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.maze.kit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author m1v3rpwn
 */
public class Soldier extends Kit {

    public Soldier() {
        boots = new ItemStack(Material.CHAINMAIL_BOOTS);
        legs = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        helm = new ItemStack(Material.CHAINMAIL_HELMET);
        sword = new ItemStack(Material.STONE_SWORD);
        inv = new ItemStack[0];
        effects = new PotionEffect[0];
        name = "Soldier";
        health = 22;
    }
}
