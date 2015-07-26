/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.maze.event;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 *
 * @author m1v3rpwn
 */
public class MobEvents implements org.bukkit.event.Listener {

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (!e.getEntityType().equals(EntityType.SKELETON) || !e.getSpawnReason().equals(SpawnReason.CUSTOM)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        e.setDroppedExp(0);
        e.getDrops().clear();
        if (!(e.getEntity() instanceof Skeleton)) {
            return;
        }
        Skeleton s = (Skeleton) e.getEntity();
        if (!me.m1v3rpwn.maze.main.Main.me.getInstance(s).checkEnd(true)) {
            me.m1v3rpwn.maze.main.Main.me.getInstance(s).getBots().remove(s);
        }
    }

}
