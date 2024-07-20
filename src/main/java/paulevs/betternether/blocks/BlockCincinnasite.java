package paulevs.betternether.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import paulevs.betternether.BetterNether;

public class BlockCincinnasite extends Block 
{
	public BlockCincinnasite(String name)
	{
		super(Material.IRON, MapColor.GOLD);
		this.setHardness(3.0F);
		this.setResistance(10.0F);
		this.setSoundType(SoundType.METAL);
		this.setTranslationKey(name);
		this.setRegistryName(name);
		this.setCreativeTab(BetterNether.BN_TAB);
	}
}
