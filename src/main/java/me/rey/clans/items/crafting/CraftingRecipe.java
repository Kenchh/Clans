package me.rey.clans.items.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;

import me.rey.clans.Main;

public abstract class CraftingRecipe extends ShapedRecipe implements Listener {

    public static Map<ItemStack, List<CraftingRecipe>> recipes = new HashMap<ItemStack, List<CraftingRecipe>>();
    private Map<Character, ItemStack> exactIntegrients = new HashMap<Character, ItemStack>();
   
  
    public CraftingRecipe(ItemStack result) {
        super(result);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }
    
    public abstract CraftingRecipe init();
  
    @Override
    public CraftingRecipe setIngredient(char key, Material ingredient) {
        super.setIngredient(key, ingredient);
        this.exactIntegrients.put(key, new ItemStack(ingredient));
        return this;
    }

    @SuppressWarnings("deprecation")
	@Override
    public CraftingRecipe setIngredient(char key, MaterialData ingredient) {
        super.setIngredient(key, ingredient);
        this.exactIntegrients.put(key, new ItemStack(ingredient.getItemType(), 1, ingredient.getData()));
        return this;
    }

    @SuppressWarnings("deprecation")
	@Override
    public CraftingRecipe setIngredient(char key, Material ingredient, int raw) {
        super.setIngredient(key, ingredient, raw);
        this.exactIntegrients.put(key, new ItemStack(ingredient, 1, (short) raw));
        return this;
    }

    @SuppressWarnings("deprecation")
	public CraftingRecipe setIngredient(char key, ItemStack item) {
        super.setIngredient(key, item.getType(), item.getDurability());
        this.exactIntegrients.put(key, item);
        return this;
    }
  
    public boolean equals(ItemStack[] matrix) {
        String[] shape = super.getShape();
        for (int y = 0; y < shape.length; y++) {
            String line = shape[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                int i = y * line.length() + x;      
                ItemStack item0 = matrix[i];
                ItemStack item1 = this.exactIntegrients.get(c);
                if (item0 != null && item1 != null) {
                    if (item0.getType() != item1.getType() || item0.getDurability() != item1.getDurability() || !item0.getItemMeta().equals(item1.getItemMeta())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
  
    public void register() {
        Bukkit.addRecipe(this);
        ItemStack result = super.getResult();
        List<CraftingRecipe> list = CraftingRecipe.recipes.get(result);
        if (list == null) {
            list = new ArrayList<CraftingRecipe>();
        }
        if (!list.contains(this)) {
            list.add(this);
            CraftingRecipe.recipes.put(result, list);
        }
    }
  
    public static List<CraftingRecipe> getRecipes(ItemStack item) {
        return CraftingRecipe.recipes.get(item);
    }
    
    @EventHandler
    public void process(PrepareItemCraftEvent event) {
        CraftingInventory inv = event.getInventory();
        ItemStack result = inv.getResult();
        
        if(!(this instanceof IExtraCraft)) {
	        for(ItemStack found : recipes.keySet()) {
	        	if(!(this instanceof IExtraCraft) && found != null && result != null && found.getType().equals(result.getType())) {
	        		inv.setResult(null);  
	        	}
	        }
        }
        
        List<CraftingRecipe> recipes = CraftingRecipe.getRecipes(result);
        if (recipes != null) {
            for (CraftingRecipe recipe : recipes) {
                if (recipe.equals(inv.getMatrix())) {
                    inv.setResult(recipe.getResult());
                    return;
                }
            }
        }
    }
    
    public interface IExtraCraft {
    	
    }
}
