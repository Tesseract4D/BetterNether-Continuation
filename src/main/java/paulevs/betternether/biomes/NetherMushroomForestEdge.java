package paulevs.betternether.biomes;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegistry;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.world.BNWorldGenerator;

import java.util.Random;

public class NetherMushroomForestEdge extends NetherMushroomForest
{
	public NetherMushroomForestEdge(String name)
	{
		super(name);
		setFogColor(new Vec3d(0.78, 0.47, 0.61));
	}
	
	public boolean isEdge() {
		return true;
	}

	@Override
	public void genFloorObjects(World world, BlockPos pos, Random random)
	{
		if (random.nextFloat() <= plantDensity)
		{
			if (world.getBlockState(pos).getBlock() == BlocksRegistry.BLOCK_NETHER_MYCELIUM)
			{
				if (BNWorldGenerator.hasRedMushroomGen && random.nextInt(100) == 0)
					BNWorldGenerator.redMushroomGen.generate(world, pos, random);
				else if (BNWorldGenerator.hasBrownMushroomGen && random.nextInt(100) == 0)
					BNWorldGenerator.brownMushroomGen.generate(world, pos, random);
				else if (BNWorldGenerator.hasOrangeMushroomGen && random.nextInt(80) == 0)
					BNWorldGenerator.orangeMushroomGen.generate(world, pos, random);
				else if (BNWorldGenerator.hasRedMoldGen && random.nextInt(64) == 0)
					BNWorldGenerator.redMoldGen.generate(world, pos, random);
				else if (BNWorldGenerator.hasGrayMoldGen && random.nextInt(60) == 0)
					BNWorldGenerator.grayMoldGen.generate(world, pos, random);
				else if (random.nextInt(16) == 0)
					if (random.nextBoolean())
						world.setBlockState(pos.up(), Blocks.RED_MUSHROOM.getDefaultState());
					else
						world.setBlockState(pos.up(), Blocks.BROWN_MUSHROOM.getDefaultState());
			}
			else if (ConfigLoader.isTerrain(world.getBlockState(pos).getBlock()) && random.nextBoolean())
			{
				world.setBlockState(pos.up(), BlocksRegistry.BLOCK_NETHER_GRASS.getDefaultState());
			}
		}
	}
	
	@Override
	public void genSurfColumn(World world, BlockPos pos, Random random)
	{
		if (world.getBlockState(pos).getBlock() == Blocks.NETHERRACK) {
			world.setBlockState(pos, BlocksRegistry.BLOCK_NETHER_MYCELIUM.getDefaultState());
		}
	}
}
