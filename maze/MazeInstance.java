package maze;

import java.util.ArrayList;
import kit.Kit;
import main.Stats;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class MazeInstance {

    private MazeGenerator gen;
    private ArrayList<Player> pl, spec;
    private boolean single;
    private boolean running;
    private boolean counting;
    private ArrayList<Skeleton> bots;
    private PotionEffect pe;

    public MazeInstance(int xmin, int zmin) {
        running = false;
        counting = false;
        pl = new ArrayList<>();
        spec = new ArrayList<>();
        bots = new ArrayList<>();
        gen = new MazeGenerator(xmin, xmin, zmin, zmin, main.Main.MAZE_ALT, main.Main.me.getServer().getWorlds().get(0), Aesthetic.HAY);
    }

    public MazeInstance(MazeGenerator g) {
        gen = g;
        gen.init();
        pl = new ArrayList<>();
        spec = new ArrayList<>();
        bots = new ArrayList<>();
        running = false;
        counting = false;
    }

    public void start(ArrayList<Player> players) {
        pl = players;
        main.Main.me.lob.getPlayers().removeAll(pl);
        spec = new ArrayList<>();
        bots = new ArrayList<>();
        single = (pl.size() == 1);
//        int side = 48;
        gen.xmax = gen.xmin + getSideLength(pl.size());
        gen.zmax = gen.zmin + getSideLength(pl.size());
        gen.a = Aesthetic.getRandomAesthetic();
        gen.w.setTime(6000);
        gen.init();
        for (Player p : pl) {
            for (Player p2 : pl) {
                if (!p.equals(p2)) {
                    p.hidePlayer(p2);
                }
            }
            p.teleport(new Location(gen.w, (gen.xmax + gen.xmin) / 2, gen.alt + 2, (gen.zmax + gen.zmin) / 2));
            main.Main.me.chosenKits.get(p).apply(p);
            Stats.incrementGames(p, single);
        }
        new CountdownRunnable(10, ChatColor.YELLOW + "You have <count> seconds to get into the maze!").runTaskTimer(main.Main.me, 20, 20).getTaskId();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : pl) {
                    if (p.getLocation().getY() > gen.alt + MazeGenerator.HEIGHT) {
                        p.setFireTicks(200);
                        p.sendMessage(ChatColor.YELLOW + "What're you doing up there?");
                    }
                    for (Player play : pl) {
                        if (!p.equals(play)) {
                            p.showPlayer(play);
                        }
                    }
                }
                sendMessage(ChatColor.GREEN + "The game has begun! Fight to survive!");
                for (int i = 0; i < 9 - pl.size(); i++) {
                    double x = Math.random() * (gen.xmax - gen.xmin) + gen.xmin;
                    double z = Math.random() * (gen.zmax - gen.zmin) + gen.zmin;
                    if (!gen.w.getBlockAt((int) x, gen.alt + 1, (int) z).getType().equals(Material.AIR)) {
                        for (int xoff = -5; xoff < 6; xoff++) {
                            for (int zoff = -5; zoff < 6; zoff++) {
//                                if                        v   Block is Air                                                     and      new x is in the maze             and       new z is in the maze
                                if (gen.w.getBlockAt((int) x + xoff, gen.alt + 1, (int) z + zoff).getType().equals(Material.AIR) && gen.xmin < x + xoff && x + xoff < gen.xmax && gen.zmin < z + zoff && z + zoff < gen.zmax) {
                                    x += xoff;
                                    z += zoff;
                                }
                            }
                        }
                    }

                    Skeleton skel = gen.w.spawn(new Location(gen.w, x, gen.alt + 1, z), Skeleton.class);
                    skel.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
//                        Well this line's a mess. v Determines target type     v Random Player                                   v Random Skele, or nothing if there are no skeles
                    skel.setTarget((Math.random() < (pl.size() + 1.0) / 9) ? pl.get((int) (Math.random() * pl.size())) : (bots.isEmpty() ? null : bots.get((int) (Math.random() * bots.size()))));
                    skel.setCustomName(ChatColor.RED + "Enemy");
                    skel.setCustomNameVisible(true);
                    Kit k = main.Main.me.getZombieKit();
                    k.apply(skel);
                    bots.add(skel);
//                        skel.teleport(pl.get(0));
                }
            }
        }.runTaskLater(main.Main.me, 220);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : spec) {
                    p.getVelocity().setY(0);
                    p.setFlying(true);
                    if (p.getLocation().getY() > gen.alt + MazeGenerator.HEIGHT + 2 || p.getLocation().getX() < gen.xmin || p.getLocation().getX() > gen.xmax || p.getLocation().getZ() < gen.zmin || p.getLocation().getZ() > gen.zmax) {
                        p.teleport(new Location(gen.w, (gen.xmax + gen.xmin) / 2, gen.alt + 1, (gen.zmax + gen.zmin) / 2));
                    }
                    p.setHealth(p.getMaxHealth());
                    p.setFireTicks(0);
                }
                for (Skeleton sk : bots) {
                    if (sk.getTarget() instanceof Player && !pl.contains((Player) sk.getTarget())) {
                        sk.setTarget((!pl.isEmpty() ? pl.get((int) (Math.random() * pl.size())) : null));
                    }
                    if (sk.getTarget() instanceof Skeleton && !bots.contains((Skeleton) sk.getTarget())) {
                        sk.setTarget((!pl.isEmpty() ? pl.get((int) (Math.random() * pl.size())) : null));
                    }
                }
                setSignLine(2, ChatColor.YELLOW + "Players: " + pl.size());
                if (!running) {
                    cancel();
                }
            }
        }.runTaskTimer(main.Main.me, 40, 40);
        setSignLine(1, ChatColor.YELLOW + "Game running!");
        setSignLine(2, ChatColor.YELLOW + "Players: " + pl.size());
        setSignLine(3, ChatColor.YELLOW + "Click to view!");
        running = true;
    }

    public void finish(final Player winner) {
        winner.sendMessage(ChatColor.GREEN + "You win!");
        Stats.incrementWins(winner, single);
        running = false;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p1 : spec) {
                    for (Player p2 : spec) {
                        p1.showPlayer(p2);
                    }
                    winner.showPlayer(p1);
                    p1.teleport(main.Main.me.lob.getSpawn());
                    p1.getInventory().clear();
                    p1.getEquipment().clear();
                    p1.setAllowFlight(false);
                    p1.setFlying(false);
                    p1.setMaxHealth(20);
                    p1.setHealth(20);
                    p1.setHealthScaled(false);
                    for (PotionEffect pe : p1.getActivePotionEffects()) {
                        p1.addPotionEffect(new PotionEffect(pe.getType(), 1, 1), true);
                    }
                }
                winner.teleport(main.Main.me.lob.getSpawn());
                winner.getInventory().clear();
                winner.getEquipment().clear();
                winner.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
                winner.setMaxHealth(20);
                winner.setHealth(20);
                winner.setHealthScaled(false);
                for (PotionEffect pe : winner.getActivePotionEffects()) {
                    winner.addPotionEffect(new PotionEffect(pe.getType(), 1, 1), true);
                }
                main.Main.me.lob.getPlayers().addAll(spec);
                main.Main.me.lob.getPlayers().add(winner);
                pl.clear();
                spec.clear();
                bots.clear();
                setSignLine(1, ChatColor.DARK_AQUA + "Needs players");
                setSignLine(2, ChatColor.DARK_AQUA + "Players: 0");
                setSignLine(3, ChatColor.DARK_AQUA + "Click to join");
                running = false;
            }
        }.runTaskLater(main.Main.me, 100);

    }

    public void finish() {
        sendMessage(ChatColor.RED + "All players died! Nobody wins!");
        running = false;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p1 : spec) {
                    for (Player p2 : spec) {
                        p1.showPlayer(p2);
                    }
                    p1.teleport(main.Main.me.lob.getSpawn());
                    p1.setAllowFlight(false);
                    p1.setFlying(false);
                    p1.setMaxHealth(20);
                    p1.setHealth(20);
                    p1.setHealthScaled(false);
                    for (PotionEffect pe : p1.getActivePotionEffects()) {
                        p1.addPotionEffect(new PotionEffect(pe.getType(), 1, 1), true);
                    }
                }
                for (Skeleton s : bots) {
                    s.remove();
                }
                main.Main.me.lob.getPlayers().addAll(spec);
                setSignLine(1, ChatColor.DARK_AQUA + "Needs players");
                setSignLine(2, ChatColor.DARK_AQUA + "Players: 0");
                setSignLine(3, ChatColor.DARK_AQUA + "Click to join");
                pl.clear();
                spec.clear();
                bots.clear();
                running = false;
            }
        }.runTaskLater(main.Main.me, 100);

    }

    public boolean checkEnd(boolean deadmob) {
        if (pl.size() == 1 && bots.size() == (deadmob ? 1 : 0)) {
            finish(pl.get(0));
            return true;
        }
        if (pl.isEmpty()) {
            finish();
            return true;
        }
        return false;
    }

    public void sendMessage(String msg) {
        for (Player p : pl) {
            p.sendMessage(msg);
        }
        for (Player p : spec) {
            p.sendMessage(msg);
        }
    }

    public void sendSpectMessage(String msg) {
        for (Player p : spec) {
            p.sendMessage(msg);
        }
    }

    public void addPlayer(Player p) {
        if (running) {
            addSpectator(p);
            return;
        }
        if (pl.contains(p)) {
            return;
        }
        pl.add(p);
        p.sendMessage(ChatColor.GREEN + "You have joined " + getSignLine(0));
        setSignLine(2, ChatColor.GREEN + "Players: " + pl.size());
        setSignLine(3, ChatColor.GREEN + "Click to join");
        if (pl.size() >= 1 && !counting) {
            counting = true;
            new BukkitRunnable() {
                private int count = 15;

                @Override
                public void run() {
                    setSignLine(1, ChatColor.GREEN + "Starts in " + count);
                    if (count == 0) {
                        start(pl);
                        counting = false;
                        cancel();
                    }
                    if (pl.isEmpty()) {
                        setSignLine(1, ChatColor.DARK_AQUA + "Needs players");
                        setSignLine(2, ChatColor.DARK_AQUA + "Players: 0");
                        setSignLine(3, ChatColor.DARK_AQUA + "Click to join");
                        counting = false;
                        cancel();
                    }
                    count--;
                }
            }.runTaskTimer(main.Main.me, 0, 20);
        }
    }

    public void addSpectator(Player p) {
        if (pl.contains(p)) {
            pl.remove(p);
        }
        if (!spec.contains(p)) {
            spec.add(p);
        }
        p.setHealth(p.getMaxHealth());
        p.getInventory().clear();
        p.setAllowFlight(true);
        p.setFlying(true);
        for (Player play : pl) {
            play.hidePlayer(p);
        }
        for (Player play : spec) {
            if (play.equals(p)) {
                continue;
            }
            p.hidePlayer(play);
            play.hidePlayer(p);
        }
        p.teleport(new Location(gen.w, gen.xmin, gen.alt + MazeGenerator.HEIGHT + 1, gen.zmin));
    }

    public void regen(int sidelength, Aesthetic aes) {
        gen.xmax = gen.xmin + sidelength;
        gen.zmax = gen.zmin + sidelength;
        gen.a = aes;
        gen.init();
    }

    public boolean isSpectating(Player p) {
        return spec.contains(p);
    }

    public boolean isPlaying(Player p) {
        return pl.contains(p);
    }

    public boolean isRunning() {
        return running;
    }

    public ArrayList<Player> getPlayers() {
        return pl;
    }

    public ArrayList<Player> getSpects() {
        return spec;
    }

    public ArrayList<Skeleton> getBots() {
        return bots;
    }

    public MazeGenerator getGen() {
        return gen;
    }

    public Sign getLobbySign() {
        int x = -41 - (gen.xmin / main.Main.GAME_GRID_TILE_SIZE);
        int y = 64 + (gen.zmin / main.Main.GAME_GRID_TILE_SIZE);
        int z = -45;
        Sign ret = (Sign) gen.w.getBlockAt(x, y, z).getState();
        ret.update();
        return ret;
    }

    public String getSignLine(int line) {
        int x = -41 - (gen.xmin / main.Main.GAME_GRID_TILE_SIZE);
        int y = 64 + gen.zmin / main.Main.GAME_GRID_TILE_SIZE;
        int z = -45;
        Sign ret = (Sign) gen.w.getBlockAt(x, y, z).getState();
        return ret.getLine(line);
    }

    public void setSignLine(int line, String text) {
        int x = -41 - (gen.xmin / main.Main.GAME_GRID_TILE_SIZE);
        int y = 64 + gen.zmin / main.Main.GAME_GRID_TILE_SIZE;
        int z = -45;
        Sign ret = (Sign) gen.w.getBlockAt(x, y, z).getState();
        ret.setLine(line, text);
        ret.update();
    }

    public static int getSideLength(int players) {
        switch (players) {
            case 0:
            case 1:
            case 2:
            case 3:
                return 32;
            case 4:
            case 5:
            case 6:
            case 7:
                return 48;
            case 8:
            case 9:
                return 64;
            default:
                return 80;
        }
    }

    public class CountdownRunnable extends BukkitRunnable {

        private int count;
        String text;

        public CountdownRunnable(int start, String msg) {
            count = start;
            text = msg;
        }

        @Override
        public void run() {
            String msg = text.replaceAll("<count>", count + "");
            sendMessage(msg);
            count--;
            if (count == 0) {
                cancel();
            }
        }
    }
}
