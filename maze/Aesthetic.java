package maze;

import org.bukkit.Material;

public enum Aesthetic {

    HAY(Material.HAY_BLOCK, Material.AIR, Material.DIRT, Material.AIR),
    DESERT(Material.SAND, Material.AIR, Material.SANDSTONE, Material.AIR),
    FOREST(Material.LOG, Material.VINE, Material.GRASS, Material.LEAVES),
    MINE(Material.STONE, Material.TORCH, Material.COBBLESTONE, Material.COBBLESTONE),
    NETHER(Material.NETHERRACK, Material.AIR, Material.NETHER_BRICK, Material.FIRE),
    END(Material.ENDER_STONE, Material.REDSTONE_TORCH_ON, Material.ENDER_PORTAL_FRAME, Material.OBSIDIAN);

    private Material wall, hall, ground, top;

    private Aesthetic(Material wallMat, Material hallMat, Material groundMat, Material topMat) {
        wall = wallMat;
        hall = hallMat;
        ground = groundMat;
        top = topMat;
    }

    public Material getWall() {
        return wall;
    }

    public Material getHall() {
        return hall;
    }

    public Material getGround() {
        return ground;
    }

    public Material getTop() {
        return top;
    }

    public static Aesthetic getRandomAesthetic() {
        double rand = Math.random();
        if (rand < 1.0 / 5) {
            return Aesthetic.HAY;
        }
        if (rand < 2.0 / 5) {
            return Aesthetic.DESERT;
        }
        if (rand < 3.0 / 5) {
            return Aesthetic.FOREST;
        }
        return Aesthetic.MINE;
    }
}
