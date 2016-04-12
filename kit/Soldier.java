package kit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class Soldier extends Kit {

    public Soldier() {
        boots = new ItemStack(Material.CHAINMAIL_BOOTS);
        legs = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        helm = new ItemStack(Material.CHAINMAIL_HELMET);
        sword = new ItemStack(Material.STONE_SWORD);
        inv = new ItemStack[0];
        effects = new PotionEffect[0];
        name = "Soldier";
        health = 22;
    }
}
