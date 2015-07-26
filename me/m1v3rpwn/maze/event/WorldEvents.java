/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.maze.event;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

/**
 *
 * @author m1v3rpwn
 */
public class WorldEvents implements org.bukkit.event.Listener {
    
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE) || e.getBlock().getType().equals(Material.WALL_SIGN)) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            e.setCancelled(true);
        }
    }
    
}
