package event;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;

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
        if (!main.Main.me.getInstance(s).checkEnd(true)) {
            main.Main.me.getInstance(s).getBots().remove(s);
        }
    }

}
