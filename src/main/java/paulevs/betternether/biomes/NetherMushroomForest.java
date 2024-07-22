package paulevs.betternether.biomes;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.world.BNWorldGenerator;

import java.util.Random;

public class NetherMushroomForest extends NetherBiome
{
	public NetherMushroomForest(String name)
	{
		super(name);
	}

	@Override
	public void genSurfColumn(World world, BlockPos pos, Random random)
	{
		if (world.getBlockState(pos).getBlock() == Blocks.NETHERRACK)
		{
			if (BlocksRegister.BLOCK_NETHER_MYCELIUM != Blocks.AIR)
				world.setBlockState(pos, BlocksRegister.BLOCK_NETHER_MYCELIUM.getDefaultState());
			else
				world.setBlockState(pos, Blocks.MYCELIUM.getDefaultState());
		}
	}

	@Override
	public void genFloorObjects(World world, BlockPos pos, Random random)
	{
		if (random.nextFloat() <= plantDensity && world.getBlockState(pos).getBlock() == BlocksRegister.BLOCK_NETHER_MYCELIUM || world.getBlockState(pos).getBlock() == Blocks.MYCELIUM)
		{
			if (BNWorldGenerator.hasRedMushroomGen && random.nextInt(28) == 0)
				BNWorldGenerator.redMushroomGen.generate(world, pos, random);
			else if (BNWorldGenerator.hasBrownMushroomGen && random.nextInt(24) == 0)
				BNWorldGenerator.brownMushroomGen.generate(world, pos, random);
			else if (BNWorldGenerator.hasOrangeMushroomGen && random.nextInt(40) == 0)
				BNWorldGenerator.orangeMushroomGen.generate(world, pos, random);
			else if (BNWorldGenerator.hasRedMoldGen && random.nextInt(24) == 0)
				BNWorldGenerator.redMoldGen.generate(world, pos, random);
			else if (BNWorldGenerator.hasGrayMoldGen && random.nextInt(20) == 0)
				BNWorldGenerator.grayMoldGen.generate(world, pos, random);
			else if (random.nextInt(6) == 0)
				if (random.nextBoolean())
					world.setBlockState(pos.up(), Blocks.RED_MUSHROOM.getDefaultState());
				else
					world.setBlockState(pos.up(), Blocks.BROWN_MUSHROOM.getDefaultState());
		}
	}
	
	@Override
	public void genWallObjects(World world, BlockPos origin, BlockPos pos, Random random)
	{
		if (random.nextFloat() <= plantDensity && BNWorldGenerator.hasLucisGen && random.nextInt(8) == 0 && ConfigLoader.isTerrain(world.getBlockState(origin).getBlock()))
			BNWorldGenerator.lucisGen.generate(world, pos, random);
	}
}
