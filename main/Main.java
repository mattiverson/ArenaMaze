package main;

import event.MobEvents;
import event.WorldEvents;
import event.PlayerEvents;
import kit.Kit;
import kit.Soldier;
import kit.Fighter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import maze.Aesthetic;
import maze.MazeInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends org.bukkit.plugin.java.JavaPlugin {

    public static Main me;
    private MazeInstance[][] insts;

    private static final ArrayList<Kit> kits;
    public static final HashMap<String, Kit> kitnames;
    public HashMap<Player, Kit> chosenKits;
    public HashMap<String, String> prefixes;
    public ArrayList<Player> players;
    public PlayerEvents events;
    public Lobby lob;
    public YamlConfiguration stats, prems;
    private File statsFile;
    public static final int GAME_GRID_WIDTH = 4;
    public static final int GAME_GRID_TILE_SIZE = 100;
    public static final int MAZE_ALT = 70;

    static {
        kits = new ArrayList<>();
        kits.add(new Soldier());
        kits.add(new Fighter());

        kitnames = new HashMap<>();
        kitnames.put("soldier", kits.get(0));
        kitnames.put("fighter", kits.get(1));
    }

    @Override
    public void onEnable() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(events = new PlayerEvents(this), this);
        pm.registerEvents(new MobEvents(), this);
        pm.registerEvents(new WorldEvents(), this);
        lob = new Lobby(this);
        pm.registerEvents(lob, this);
        me = this;
        chosenKits = new HashMap<>();
        prefixes = new HashMap<>();
        players = new ArrayList<>();
        insts = new MazeInstance[4][4];
        for (int z = 0; z < 4; z++) {
            for (int x = 0; x < 4; x++) {
                insts[z][x] = new MazeInstance(x * GAME_GRID_TILE_SIZE, z * GAME_GRID_TILE_SIZE);
            }
        }
        statsFile = new File(this.getDataFolder(), "stats.yml");
        stats = YamlConfiguration.loadConfiguration(statsFile);
        File premsFile = new File(this.getDataFolder(), "premiums.yml");
        prems = YamlConfiguration.loadConfiguration(premsFile);
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }
        if (!statsFile.exists()) {
            try {
                statsFile.createNewFile();
                stats.createSection("wins");
                stats.createSection("singlewins");
                stats.save(statsFile);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (!premsFile.exists()) {
            try {
                premsFile.createNewFile();
                prems.createSection("help");
                prems.createSection("hurt");
                prems.createSection("rand");
                prems.save(premsFile);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        prefixes.put("EmWan", "[Owner] " + ChatColor.DARK_PURPLE + ChatColor.UNDERLINE);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(ChatColor.DARK_GREEN + "Like the server? Donate at http://arenamaze.buycraft.net !");
                }
            }
        }.runTaskTimer(this, 9000, 12000);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(ChatColor.DARK_GREEN + "Enjoying Arena Maze? Bring your friends!");
                }
            }
        }.runTaskTimer(this, 3000, 12000);
    }

    @Override
    public void onDisable() {
        for (Player p : this.getServer().getOnlinePlayers()) {
            p.kickPlayer(ChatColor.RED + "The server needed to restart! You'll be able to come back shortly!");
        }
        try {
            stats.save(new File(this.getDataFolder(), "stats.yml"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        File prfxs = new File(getDataFolder(), "prefixes.yml");
        try {
            if (!prfxs.exists()) {
                prfxs.createNewFile();
            }
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(prfxs);
            for (Entry e : prefixes.entrySet()) {
                cfg.set((String) e.getKey(), e.getValue());
            }
            cfg.save(prfxs);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command Command, String label, String[] args) {
//        Player p = (Player) sender;
//        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldParticles("reddust", (float) p.getLocation().getX(), (float) p.getLocation().getY(), (float) p.getLocation().getZ(), 0.2f, 0.2f, 0.2f, 0, 10));
        if (Commands.hasCommand(label.toLowerCase())) {
            return Commands.execute(sender, label.toLowerCase(), args);
        }
        return false;
    }

    public void startInstance(ArrayList<Player> plays, Aesthetic aes) {
        int sidelength = MazeInstance.getSideLength(plays.size());
        MazeInstance inst = getFirstOpenInstance();
        inst.regen(sidelength, aes);
        inst.start(plays);
    }

    public MazeInstance getInstance(Player p) {
        for (MazeInstance[] ins : insts) {
            for (MazeInstance i : ins) {
                if (i.getPlayers().contains(p) || i.getSpects().contains(p)) {
                    return i;
                }
            }
        }
        return null;
    }

    public MazeInstance getInstance(Skeleton s) {
        for (MazeInstance[] ins : insts) {
            for (MazeInstance i : ins) {
                if (i.getBots().contains(s)) {
                    return i;
                }
            }
        }
        return null;
    }

    public MazeInstance getInstance(int xmin, int zmin) {
        return insts[zmin / GAME_GRID_TILE_SIZE][xmin / GAME_GRID_TILE_SIZE];
    }

    public MazeInstance getFirstOpenInstance() {
        for (int z = 0; z < insts.length; z++) {
            for (int x = 0; x < insts[z].length; x++) {
                if (!insts[z][x].isRunning()) {
                    return insts[z][x];
                }
            }
        }
        return null;
    }

    public Kit getZombieKit() {
        return kits.get((int) (Math.random() * (kits.size() > 3 ? 3 : kits.size())));
    }
}
