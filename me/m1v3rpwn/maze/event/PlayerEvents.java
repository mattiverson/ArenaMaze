/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.maze.event;

import java.io.File;
import me.m1v3rpwn.maze.main.Main;
import me.m1v3rpwn.maze.main.Stats;
import me.m1v3rpwn.maze.maze.MazeInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author m1v3rpwn
 */
public class PlayerEvents implements org.bukkit.event.Listener {

    Main main;

    public PlayerEvents(Main m) {
        main = m;
    }

    @EventHandler
    public void quickRespawn(PlayerDeathEvent e) {
        if (!main.players.contains(e.getEntity())) {
            return;
        }
        e.setDeathMessage(null);
        final Player p = e.getEntity();
        p.setHealth(p.getMaxHealth());
        p.sendMessage(ChatColor.DARK_RED + "You died! Better luck next time!");
        p.getEquipment().clear();
        p.getInventory().clear();
        p.setFireTicks(1);
        p.teleport(new Location(p.getWorld(), p.getLocation().getX(), -5, p.getLocation().getZ()));
        new BukkitRunnable() {
            public void run() {
                if (main.getInstance(p) == null) {
                    p.teleport(main.lob.getSpawn());
                } else {
                    main.getInstance(p).addSpectator(p);
                    main.getInstance(p).checkEnd(false);
                }
            }
        }.runTaskLater(main, 10);
    }

    @EventHandler
    public void noItemDrops(PlayerDropItemEvent e) {
        if (!main.players.contains(e.getPlayer())) {
            return;
        }
        if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        e.setCancelled(true);
    }

    public void playerInit(Player p) {
        main.chosenKits.put(p, main.getZombieKit());
        Scoreboard score = main.getServer().getScoreboardManager().getNewScoreboard();
        p.setScoreboard(score);
        Objective stats = score.registerNewObjective(ChatColor.GREEN + "Stats", "dummy");
        stats.setDisplaySlot(DisplaySlot.SIDEBAR);
        stats.getScore(ChatColor.AQUA + "Solo games:").setScore(Stats.getGames(p, true));
        stats.getScore(ChatColor.AQUA + "Solo wins:").setScore(Stats.getWins(p, true));
        stats.getScore(ChatColor.AQUA + "Group games:").setScore(Stats.getGames(p, false));
        stats.getScore(ChatColor.AQUA + "Group wins:").setScore(Stats.getWins(p, false));
        File prfxs = new File(main.getDataFolder(), "prefixes.yml");
        try {
            if (!prfxs.exists()) {
                prfxs.createNewFile();
            }
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(prfxs);
            if (cfg.contains(p.getName())) {
                main.prefixes.put(p.getName(), cfg.getString(p.getName()));
                StringBuilder listName = new StringBuilder();
                listName.append(p.getName().equals("EmWan") ? ChatColor.DARK_PURPLE + "" + ChatColor.UNDERLINE : main.prefixes.get(p.getName()));
                listName.append(p.getName().length() + listName.length() > 16 ? p.getName().substring(0, 16 - listName.length()) : p.getName());
                p.setPlayerListName(main.prefixes.get(p.getName()) + p.getName());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        Bukkit.broadcastMessage(e.getPlayer().getAddress().getAddress().getHostAddress());
    }

    @EventHandler
    public void instanceChat(PlayerChatEvent e) {
        if (!main.players.contains(e.getPlayer())) {
            return;
        }
        MazeInstance inst = main.getInstance(e.getPlayer());
        String pfx = main.prefixes.containsKey(e.getPlayer().getName()) ? main.prefixes.get(e.getPlayer().getName()) : "";
        StringBuilder mess = new StringBuilder();
        mess.append(pfx);
        mess.append("<" + e.getPlayer().getName() + ">");
        mess.append(ChatColor.RESET + " ");
        mess.append(e.getMessage());
        if (inst == null) {
            e.setCancelled(true);
            main.lob.sendMessage(mess.toString());
            return;
        }
        e.setCancelled(true);
        if (inst.isPlaying(e.getPlayer())) {
            inst.sendMessage(mess.toString());
        } else {
            inst.sendSpectMessage("(dead) " + mess.toString());
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getDamager();
        if (!main.players.contains(p)) {
            return;
        }
        if (p.getGameMode().equals(GameMode.SURVIVAL) && p.getAllowFlight()) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "Spectators can't do that!");
        }
        if (main.getInstance(p) == null) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "You can't fight outside an arena!");
        }
        if (p.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            e.setDamage(e.getDamage() * 0.65);
        }
    }

    @EventHandler
    public void helpCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().equals("/help")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.GOLD + "Command help:\n" + ChatColor.AQUA + "/kit to see your available kits\n" + ChatColor.AQUA + "/kit <kit name> to choose a kit\n" + ChatColor.AQUA + "/leave to leave an arena\n" + ChatColor.AQUA + "/global to say something in global chat\n" + ChatColor.AQUA + "/report to report a bug or cheater to admins");
            e.getPlayer().sendMessage(ChatColor.GOLD + "How to play: \n" + ChatColor.DARK_AQUA + "Left click on a sign to join an arena\n" + ChatColor.DARK_AQUA + "Right click on a sign to see the list of players in that arena");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playCommand(PlayerCommandPreprocessEvent e) {
        if (!e.getMessage().toLowerCase().startsWith("/play")) {
            return;
        }
        if (e.isCancelled()) {
            return;
        }
        String game = e.getMessage().substring(6);
        Player p = e.getPlayer();
        if (game.equalsIgnoreCase("maze") || game.equalsIgnoreCase("arenamaze")) {
            if (main.players.contains(p)) {
                p.sendMessage(ChatColor.RED + "You're already in that game!");
                return;
            }
//            p.sendMessage(ChatColor.GREEN + "Joining Arena Maze!");
            main.players.add(p);
            main.lob.intro(p);
            main.events.playerInit(p);
            p.teleport(main.lob.getSpawn());
        } else if (main.players.contains(p)) {
            main.events.exitPlayer(p);
            main.players.remove(p);
        }
    }
    
    @EventHandler
    public void combatLog(PlayerQuitEvent e) {
        if (!main.players.contains(e.getPlayer())) {
            return;
        }
        exitPlayer(e.getPlayer());
    }
    
    public void exitPlayer(Player p) {
        MazeInstance inst = main.getInstance(p);
        if (inst == null) {
            return;
        }

        if (inst.isPlaying(p)) {
            inst.getPlayers().remove(p);
        } else {
            inst.getSpects().remove(p);
        }
        inst.checkEnd(false);
        
        if (main.lob.getPlayers().contains(p)) {
            main.lob.getPlayers().remove(p);
        }
    }
}
