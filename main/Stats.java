package main;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class Stats {

    public static void incrementWins(Player p, boolean single) {
        ConfigurationSection wins = main.Main.me.stats.getConfigurationSection(single ? "singlewins" : "wins");
        if (wins == null) {
            wins = main.Main.me.stats.createSection(single ? "singlewins" : "wins");
        }
        int win = 0;
        if (!wins.contains(p.getName())) {
            wins.set(p.getName(), 1);
        } else {
            win = wins.getInt(p.getName());
            wins.set(p.getName(), win + 1);
        }
        win++;
        p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(single ? ChatColor.AQUA + "Solo wins:" : ChatColor.AQUA + "Group wins:").setScore(win);
    }

    public static int getWins(Player p, boolean single) {
        ConfigurationSection wins = main.Main.me.stats.getConfigurationSection(single ? "singlewins" : "wins");
        if (wins == null) {
            return 0;
        }
        if (!wins.contains(p.getName())) {
            return 0;
        } else {
            return wins.getInt(p.getName());
        }
    }

    public static void incrementGames(Player p, boolean single) {
        ConfigurationSection wins = main.Main.me.stats.getConfigurationSection(single ? "singlegames" : "games");
        if (wins == null) {
            wins = main.Main.me.stats.createSection(single ? "singlegames" : "games");
        }
        int game = 0;
        if (!wins.contains(p.getName())) {
            wins.set(p.getName(), 1);
        } else {
            game = wins.getInt(p.getName());
            wins.set(p.getName(), game + 1);
        }
        game++;
        p.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(single ? ChatColor.AQUA + "Solo games:" : ChatColor.AQUA + "Group games:").setScore(game);
    }

    public static int getGames(Player p, boolean single) {
        ConfigurationSection wins = main.Main.me.stats.getConfigurationSection(single ? "singlegames" : "games");
        if (wins == null) {
            return 0;
        }
        if (!wins.contains(p.getName())) {
            return 0;
        } else {
            return wins.getInt(p.getName());
        }
    }
}
