/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.m1v3rpwn.maze.maze;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

/**
 *
 * @author m1v3rpwn
 */
public class MazeGenerator {

    int xmin, zmin, xmax, zmax, alt;
    World w;
    Aesthetic a;
    public static final int HEIGHT = 4;
    public static final double SEG_CLEAR_PROB = 0.7;

    public MazeGenerator(int x1, int x2, int z1, int z2, int ymin, World world, Aesthetic ast) {
        xmin = Math.min(x1, x2);
        xmax = Math.max(x1, x2);
        zmin = Math.min(z1, z2);
        zmax = Math.max(z1, z2);
        alt = ymin;
        if (world != null) {
            w = world;
        } else {
            w = me.m1v3rpwn.maze.main.Main.me.getServer().getWorlds().get(0);
        }
        if (ast != null) {
            a = ast;
        } else {
            a = Aesthetic.getRandomAesthetic();
        }
    }

    public void init() {
        w.setTime(6000);
        clearArea();
        drawMaze();
        boundWalls();
    }

    private void clearArea() {
        for (int x = xmin + 1; x < xmax; x++) {
            for (int z = zmin + 1; z < zmax; z++) {
                for (int y = alt + 1; y <= alt + HEIGHT; y++) {
                    w.getBlockAt(x, y, z).setType(a.getWall());
                    w.getBlockAt(x, y + HEIGHT, z).setType(Material.AIR);
                }
            }
        }
    }

    private void drawMaze() {
        int xmid = (xmin + xmax) / 2;
        int zmid = (zmin + zmax) / 2;
        int side = xmax - xmin;
        ArrayList<Location> starts = new ArrayList<>();
        starts.add(new Location(w, xmin + 1.0, alt + 1.0, zmin + 1.0));
        starts.add(new Location(w, xmin + 1.0, alt + 1.0, zmid + 0.0));
        starts.add(new Location(w, xmin + 1.0, alt + 1.0, zmax - 1.0, -90, 0));
        starts.add(new Location(w, xmid + 0.0, alt + 1.0, zmax - 1.0, -90, 0));
        starts.add(new Location(w, xmax - 1.0, alt + 1.0, zmax - 1.0, 180, 0));
        starts.add(new Location(w, xmax - 1.0, alt + 1.0, zmid + 0.0, 180, 0));
        starts.add(new Location(w, xmax - 1.0, alt + 1.0, zmin + 1.0, 90, 0));
        starts.add(new Location(w, xmid + 0.0, alt + 1.0, zmin + 1.0, 90, 0));
        starts.add(new Location(w, xmid + 0.0, alt + 1.0, zmid + 0.0));
        ArrayList<Location> tempstarts = new ArrayList<>();
        for (int length = (side) / 4 - 1; length > 0; length -= ((side) / 4) / (side > 80 ? 8 : 4)) {
            for (Location loc : starts) {
                if (Math.random() < SEG_CLEAR_PROB) {
                    clearSegment(loc.getBlockX() - length, loc.getBlockZ(), loc.getBlockX(), loc.getBlockZ());
                    tempstarts.add(new Location(w, loc.getBlockX() - length, loc.getBlockY(), loc.getBlockZ()));
                } else {
                    tempstarts.add(loc);
                }

                if (Math.random() < SEG_CLEAR_PROB) {
                    clearSegment(loc.getBlockX(), loc.getBlockZ() - length, loc.getBlockX(), loc.getBlockZ());
                    tempstarts.add(new Location(w, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() - length));
                } else {
                    tempstarts.add(loc);
                }

                if (Math.random() < SEG_CLEAR_PROB) {
                    clearSegment(loc.getBlockX(), loc.getBlockZ(), loc.getBlockX() + length, loc.getBlockZ());
                    tempstarts.add(new Location(w, loc.getBlockX() + length, loc.getBlockY(), loc.getBlockZ()));
                } else {
                    tempstarts.add(loc);
                }

                if (Math.random() < SEG_CLEAR_PROB) {
                    clearSegment(loc.getBlockX(), loc.getBlockZ(), loc.getBlockX(), loc.getBlockZ() + length);
                    tempstarts.add(new Location(w, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() + length));
                } else {
                    tempstarts.add(loc);
                }
            }
            starts = tempstarts;
            tempstarts = new ArrayList<>();
        }
//        for (int y = alt + 1; y <= alt + HEIGHT; y++) {
//            w.getBlockAt(xmid - 1, y, zmid).setType(a.getWall());
//            w.getBlockAt(xmid, y, zmid - 1).setType(a.getWall());
//            w.getBlockAt(xmid + 1, y, zmid).setType(Material.AIR);
//            w.getBlockAt(xmid, y, zmid + 1).setType(Material.AIR);
//        }
    }

    private void clearSegment(int x1, int z1, int x2, int z2) {
        for (int x = x1; x <= x2; x++) {
            for (int z = z1; z <= z2; z++) {
                for (int y = alt + 1; y < alt + HEIGHT + 1; y++) {
                    if (y != alt + 3) {
                        w.getBlockAt(x, y, z).setType(Material.AIR);
                    } else {
                        w.getBlockAt(x, y, z).setType(a.getHall());
                    }
                }
            }
        }
    }

    private void boundWalls() {
        for (int x = xmin; x <= xmax; x++) {
            for (int y = alt; y <= alt + HEIGHT; y++) {
                w.getBlockAt(x, y, zmin).setType(a.getWall());
                w.getBlockAt(x, y, zmax).setType(a.getWall());
            }
        }
        for (int y = alt; y <= alt + HEIGHT; y++) {
            for (int z = zmin; z <= zmax; z++) {
                w.getBlockAt(xmin, y, z).setType(a.getWall());
                w.getBlockAt(xmax, y, z).setType(a.getWall());
            }
        }
        for (int x = xmin; x <= xmax; x++) {
            for (int z = zmin; z <= zmax; z++) {
                w.getBlockAt(x, alt, z).setType(a.getGround());
                w.getBlockAt(x, alt + HEIGHT + 1, z).setType(a.getTop());
            }
        }
    }

    public void destroy() {
        for (int x = xmin; x <= xmax; x++) {
            for (int z = zmin; z <= zmax; z++) {
                for (int y = alt; y <= alt + HEIGHT; y++) {
                    w.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }

    public int getXMin() {
        return xmin;
    }

    public int getZMin() {
        return zmin;
    }
}
