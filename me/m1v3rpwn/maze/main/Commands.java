/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.maze.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Level;
import me.m1v3rpwn.maze.maze.Aesthetic;
import me.m1v3rpwn.maze.maze.MazeGenerator;
import me.m1v3rpwn.maze.maze.MazeInstance;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author m1v3rpwn
 */
public class Commands {
    
    private static final HashMap<String, Boolean> commands;
    private static ArrayList<String> reports = new ArrayList<>();
    private static final ArrayList<String> blacklist = new ArrayList<>();
    private static final ArrayList<String> playerlist = new ArrayList<>();
    
    static {
        commands = new HashMap<>();
        commands.put("leave", false);
        commands.put("namechange", false);
        commands.put("kit", false);
        commands.put("report", false);
        commands.put("premallow", false);
        
        blacklist.add("fuck");
        blacklist.add("shit");
        blacklist.add("cunt");
        blacklist.add("bitch");
        blacklist.add("fag");
        
        new BukkitRunnable() {
            @Override
            public void run() {
                saveReportsToFile();
            }
        }.runTaskTimer(me.m1v3rpwn.maze.main.Main.me, 432000, 432000);
    }
    
    public static boolean hasCommand(String label) {
        return commands.containsKey(label);
    }
    
    public static boolean execute(CommandSender sender, String label, String[] args) {
        if (commands.get(label) && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + ("You don't have access to this command!"));
        }
        switch (label) {
            case "leave":
                return leaveCommand(sender);
            case "namechange":
                if (sender instanceof Player) {
                    if (args.length != 1) {
                        sender.sendMessage(ChatColor.RED + "Wrong number of parameters!");
                        return false;
                    }
                    if (!me.m1v3rpwn.maze.main.Main.me.prefixes.containsKey(sender.getName())) {
                        sender.sendMessage(ChatColor.RED + "You don't have access to this command!");
                        return true;
                    }
                    args[0] = args[0].replaceAll("&", "" + ChatColor.COLOR_CHAR);
                    sender.sendMessage(ChatColor.RED + "Name style changed to " + args[0] + "this.");
                    return nameChangeCommand(sender.getName(), args[0]);
                } else {
                    if (args.length != 2) {
                        sender.sendMessage(ChatColor.RED + "Wrong number of parameters!");
                        return false;
                    }
                    return nameChangeCommand(args[0], args[1]);
                }
            case "kit":
                if (args.length != 1 && args.length != 0) {
                    sender.sendMessage(ChatColor.RED + "Wrong number of parameters!");
                    return false;
                }
                return kitCommand(sender, args.length == 1 ? args[0] : null);
            case "report":
                return reportCommand(sender, args);
            case "a":
            case "all":
                return gameChatCommand(sender.getName(), args);
            case "premallow":
                if (sender.equals(Bukkit.getConsoleSender())) {
                    return premiumAllowCommand(args);
                }
                return false;
        }
        return false;
    }
    
    private static boolean leaveCommand(CommandSender sender) {
        Player p = (Player) sender;
        MazeInstance inst = me.m1v3rpwn.maze.main.Main.me.getInstance(p);
        if (inst == null) {
            p.sendMessage(ChatColor.RED + "You're not in an arena right now.");
            return false;
        }
        
        if (inst.isPlaying(p)) {
            inst.getPlayers().remove(p);
        } else {
            inst.getSpects().remove(p);
        }
        inst.checkEnd(false);
        p.teleport(me.m1v3rpwn.maze.main.Main.me.lob.getSpawn());
        p.sendMessage(ChatColor.GOLD + "You have left your arena.");
        return true;
    }
    
    private static boolean nameChangeCommand(String name, String prfx) {
        switch (prfx.length()) {
            case 0:
                return false;
            case 1:
            case 3:
                return false;
            case 4:
                prfx = ChatColor.getByChar(prfx.charAt(3)) + "";
            case 2:
                prfx = ChatColor.getByChar(prfx.charAt(1)) + "";
                break;
            default:
                return false;
            
        }
        me.m1v3rpwn.maze.main.Main.me.prefixes.put(name, prfx);
        return true;
    }
    
    private static boolean kitCommand(CommandSender sender, String arg) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player p = (Player) sender;
        if (arg == null) {
            p.sendMessage(ChatColor.GREEN + "Available kits: " + me.m1v3rpwn.maze.main.Main.kitnames.keySet().toString());
            return true;
        }
        if (!me.m1v3rpwn.maze.main.Main.kitnames.containsKey(arg.toLowerCase())) {
            p.sendMessage(ChatColor.RED + "That's not a kit!");
            return true;
        }
        me.m1v3rpwn.maze.main.Main.me.chosenKits.put(p, me.m1v3rpwn.maze.main.Main.kitnames.get(arg.toLowerCase()));
        p.sendMessage(ChatColor.GREEN + "You have chosen the " + me.m1v3rpwn.maze.main.Main.me.chosenKits.get(p).getName() + " kit!");
        return true;
    }
    
    private static boolean reportCommand(CommandSender sender, String[] args) {
        if (sender.equals(Bukkit.getConsoleSender())) {
            for (String s : reports) {
                Bukkit.getLogger().log(Level.INFO, s);
            }
            saveReportsToFile();
        } else if (sender.isOp() && args.length == 0) {
            for (String s : reports) {
                sender.sendMessage(s);
            }
            saveReportsToFile();
        } else {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "That report is empty!");
                return true;
            }
            if (playerlist.contains(sender.getName().toLowerCase())) {
                sender.sendMessage(ChatColor.RED + "You can no longer use the /report feature. You may contact an admin in-game if you want to use it again.");
            }
            StringBuilder rep = new StringBuilder();
            Calendar cal = new GregorianCalendar();
            rep.append("<" + sender.getName() + "> ");
            rep.append((cal.get(Calendar.HOUR) - 1) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + (cal.get(Calendar.AM_PM) == Calendar.PM ? " PM " : " AM "));
            for (String s : args) {
                for (String s1 : blacklist) {
                    if (s.contains(s1)) {
                        sender.sendMessage(ChatColor.RED + "Your report has been dropped for containing profanity.");
                        return true;
                    }
                }
                rep.append(s + " ");
            }
            sender.sendMessage(ChatColor.GREEN + "Your report has been logged, and will be viewed by a server admin soon.");
            reports.add(rep.toString());
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.isOp()) {
                    p.sendMessage(ChatColor.DARK_RED + rep.toString());
                }
            }
        }
        return true;
    }
    
    private static boolean gameChatCommand(String name, String[] args) {
        StringBuilder msg = new StringBuilder();
        for (String s : args) {
            msg.append(s + " ");
        }
        String pfx = me.m1v3rpwn.maze.main.Main.me.prefixes.containsKey(name) ? me.m1v3rpwn.maze.main.Main.me.prefixes.get(name) : "";
        StringBuilder mess = new StringBuilder();
        mess.append("(global) ");
        mess.append(pfx);
        mess.append("<" + name + ">");
        mess.append(ChatColor.RESET + " ");
        mess.append(msg.toString());
        for (Player p : me.m1v3rpwn.maze.main.Main.me.players) {
            p.sendMessage(mess.toString());
        }
        return true;
    }
    
    private static boolean premiumAllowCommand(String[] args) {
        String name = args[0];
        String type = args[1];
        int number = isNumeric(args[2]) ? Integer.parseInt(args[2]) : 5;
        YamlConfiguration prems = me.m1v3rpwn.maze.main.Main.me.prems;
        prems.getConfigurationSection(type).set(name, number);
        return true;
    }
    
    public static boolean isNumeric(String str) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }
    
    public static void saveReportsToFile() {
        try {
            Calendar cal = new GregorianCalendar();
            String datetime = (1 + cal.get(Calendar.MONTH)) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.YEAR) + "_" + (cal.get(Calendar.HOUR) - 1) + "." + cal.get(Calendar.MINUTE) + (cal.get(Calendar.AM_PM) == Calendar.AM ? " AM" : " PM");
            File reportdata = new File(me.m1v3rpwn.maze.main.Main.me.getDataFolder(), datetime + "reports.txt");
            reportdata.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(reportdata));
            for (String s : reports) {
                writer.write(s + "\n");
            }
            writer.close();
            Bukkit.getLogger().log(Level.INFO, "Report File generated as " + datetime + " reports.txt");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
