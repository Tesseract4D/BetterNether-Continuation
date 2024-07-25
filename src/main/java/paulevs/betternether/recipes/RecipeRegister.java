package paulevs.betternether.recipes;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import paulevs.betternether.blocks.BlocksRegistry;

public class RecipeRegister
{
	public static void register()
	{
		addRecipe(BlocksRegistry.BLOCK_INK_BUSH_SEED, new ItemStack(Items.DYE, 3));
		addRecipe(BlocksRegistry.BLOCK_CINCINNASITE, BlocksRegistry.BLOCK_CINCINNASITE_FORGED);
		addRecipe(Items.QUARTZ, BlocksRegistry.BLOCK_QUARTZ_GLASS);
	}
	
	private static void addRecipe(Block material, Block result)
	{
		if (material != null && result != null)
		{
			FurnaceRecipes.instance().addSmelting(Item.getItemFromBlock(material), new ItemStack(result), 0);
		}
	}
	
	private static void addRecipe(Block material, ItemStack result)
	{
		if (material != null)
		{
			FurnaceRecipes.instance().addSmelting(Item.getItemFromBlock(material), result, 0);
		}
	}
	
	private static void addRecipe(Item material, Block result)
	{
		if (result != null)
		{
			FurnaceRecipes.instance().addSmelting(material, new ItemStack(result), 0);
		}
	}
}
