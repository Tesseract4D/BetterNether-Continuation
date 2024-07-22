package paulevs.betternether.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IShearable;
import paulevs.betternether.BetterNether;
import paulevs.betternether.config.ConfigLoader;

public class BlockNetherGrass extends BlockNetherPlant
{
	public BlockNetherGrass()
	{
		super();
		this.setRegistryName("nether_grass");
		this.setTranslationKey("nether_grass");
		this.setCreativeTab(BetterNether.BN_TAB);
		this.setSoundType(SoundType.PLANT);
	}
}
