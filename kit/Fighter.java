package kit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Fighter extends Kit {

    public Fighter() {
        boots = new ItemStack(Material.AIR);
        legs = new ItemStack(Material.LEATHER_LEGGINGS);
        chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        helm = new ItemStack(Material.AIR);
        sword = new ItemStack(Material.IRON_SWORD);
        inv = new ItemStack[0];
        effects = new PotionEffect[2];
        effects[0] = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0);
        effects[1] = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0);
        name = "fighter";
        health = 18;
    }
}
