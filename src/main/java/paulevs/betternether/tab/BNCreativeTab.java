package paulevs.betternether.tab;

import java.util.Comparator;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import paulevs.betternether.blocks.BlocksRegister;

public class BNCreativeTab extends CreativeTabs
{

	public BNCreativeTab()
	{
		super("better_nether");
	}

	@Override
	public ItemStack createIcon()
	{
		return new ItemStack(BlocksRegister.BLOCK_NETHER_GRASS);
	}
	
	@Override
    public void displayAllRelevantItems(NonNullList<ItemStack> itemList)
    {
		for (Item item : ForgeRegistries.ITEMS)
        {
            item.getSubItems(this, itemList);
        }
		Comparator<? super ItemStack> comparator = new ItemComparator();
		itemList.sort(comparator);
    }
	
	class ItemComparator implements Comparator<ItemStack>
	{
		@Override
		public int compare(ItemStack stack1, ItemStack stack2)
		{
			/*String name1 = stack1.getUnlocalizedName();
			String name2 = stack2.getUnlocalizedName();
			if (name1.contains("seed") || name1.contains("spore") && !(name2.contains("seed") || name2.contains("spore")))
				return 1;
			else if (name1.contains("stair") || name1.contains("slab") || name1.contains("fence") || name1.contains("gate"))
				return -1;
			else
				return 0;*/
				//return stack1.getUnlocalizedName().length() - stack2.getUnlocalizedName().length();
			String name1 = stack1.getTranslationKey();
			String name2 = stack2.getTranslationKey();
			return name1.compareTo(name2);
		}
	}

}
