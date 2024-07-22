package paulevs.betternether.biomes;

import net.minecraft.block.BlockNetherWart;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.world.BNWorldGenerator;

import java.util.Random;

public class NetherWartForestEdge extends NetherWartForest
{
	public NetherWartForestEdge(String name)
	{
		super(name);
	}
	
	public boolean isEdge() {
		return true;
	}

	@Override
	public void genFloorObjects(World world, BlockPos pos, Random random)
	{
		if (random.nextFloat() <= plantDensity && world.getBlockState(pos).getBlock() == Blocks.SOUL_SAND)
		{
			if (BNWorldGenerator.hasWartTreeGen && random.nextInt(70) == 0)
			{
				BNWorldGenerator.wartTreeGen.generate(world, pos, random);
			} else if (BNWorldGenerator.hasWartsGen && random.nextInt(6) == 0 && ConfigLoader.isTerrain(world.getBlockState(pos).getBlock()) && world.getBlockState(pos.up()).getBlock() == Blocks.AIR)
				world.setBlockState(pos.up(), Blocks.NETHER_WART.getDefaultState().withProperty(BlockNetherWart.AGE, random.nextInt(4)));
			else if (random.nextInt(12) == 0)
				world.setBlockState(pos.up(), BlocksRegister.BLOCK_BLACK_BUSH.getDefaultState());
		}
	}
}
