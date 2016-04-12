package main;

import java.util.ArrayList;
import maze.MazeInstance;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Lobby implements org.bukkit.event.Listener {

    private Main main;
    private ArrayList<Player> pl;
    private final Location spawn;
    private int i = 1;
    private boolean autoMatch = false;

    public Lobby(Main m) {
        main = m;
        pl = new ArrayList<>();
        spawn = new Location(main.getServer().getWorlds().get(0), -50, 64, -50);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (pl.size() > 6) {
                    ArrayList<Player> p = new ArrayList<>();
                    for (int j = 0; j < pl.size() / 2; j++) {
                        p.add(pl.get((int) (Math.random() * pl.size())));
                    }
                    MazeInstance inst = main.getFirstOpenInstance();
                    inst.start(p);
                }
            }
        }.runTaskTimer(main, 600, 600);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (pl.size() > 4) {
                    sendMessage(ChatColor.YELLOW + "A match will automatically start in 5 seconds!");
                    autoMatch = true;
                } else {
                    autoMatch = false;
                }

            }
        }.runTaskTimer(main, 500, 600);
    }

    @EventHandler
    public void signClick(PlayerInteractEvent e) {
        if (!pl.contains(e.getPlayer())) {
            return;
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
            Sign sign = (Sign) e.getClickedBlock().getState();
            int xmin = -Main.GAME_GRID_TILE_SIZE * (sign.getX() + 41);
            int zmin = Main.GAME_GRID_TILE_SIZE * (sign.getY() - 64);
            if (xmin < 0 || zmin < 0 || xmin >= 4 || zmin >= 4) {
                return;
            }
            ArrayList<Player> p = main.getInstance(xmin, zmin).getPlayers();
            StringBuilder pnames = new StringBuilder();
            if (p.isEmpty()) {
                e.getPlayer().sendMessage(ChatColor.GOLD + "There are no players in this arena!");
                return;
            }
            pnames.append(ChatColor.GOLD + "Players in this arena: ");
            for (Player pl : p) {
                pnames.append(pl.getName() + ", ");
            }
            e.getPlayer().sendMessage(pnames.substring(0, pnames.length() - 2));
        }
        if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
            Sign sign = (Sign) e.getClickedBlock().getState();
            int xmin = -Main.GAME_GRID_TILE_SIZE * (sign.getX() + 41);
            int zmin = Main.GAME_GRID_TILE_SIZE * (sign.getY() - 64);
            if (main.getInstance(xmin, zmin).getPlayers().size() > 9) {
                e.getPlayer().sendMessage(ChatColor.RED + "This arena is full!");
                return;
            }
            MazeInstance cur = main.getInstance(e.getPlayer());
            if (cur != null) {
                if (cur.isPlaying(e.getPlayer())) {
                    cur.getPlayers().remove(e.getPlayer());
                } else {
                    cur.getSpects().remove(e.getPlayer());
                }
            }
            main.getInstance(xmin, zmin).addPlayer(e.getPlayer());
            pl.remove(e.getPlayer());
        }
    }

    public void intro(Player p) {
        pl.add(p);
        p.teleport(spawn);
        p.sendMessage(ChatColor.AQUA + "Welcome to Arena Maze Alpha testing!");
        p.sendMessage(ChatColor.AQUA + "Left click one of the signs to join a game, or use /help for more info!");
        p.sendMessage(ChatColor.DARK_PURPLE + "ANNOUNCEMENT: There will be a large multiplayer match on Monday at 7:00 PM EST! Until then, feel free to check out the server!");
    }

    @EventHandler
    public void FallOrBurn(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (!pl.contains((Player) e.getEntity())) {
            return;
        }
        if (e.getCause().equals(DamageCause.LAVA)) {
            e.setDamage(2000);
        } else if (e.getCause().equals(DamageCause.FALL)) {
            e.setCancelled(true);
        } else if (e.getCause().equals(DamageCause.FIRE_TICK)) {
            e.setCancelled(true);
            e.getEntity().setFireTicks(1);
        }
    }

    public Location getSpawn() {
        return spawn;
    }

    public void sendMessage(String msg) {
        for (Player p : pl) {
            p.sendMessage(msg);
        }
    }

    public ArrayList<Player> getPlayers() {
        return pl;
    }
}
