package paulevs.betternether.biomes;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.structures.plants.StructureReeds;
import paulevs.betternether.world.BNWorldGenerator;

import java.util.Random;

public class NetherBiomeJungle extends NetherBiome
{
	public NetherBiomeJungle(String name)
	{
		super(name);
		setFogColor(new Vec3d(0.24, 0.66, 0.24));
	}

	@Override
	public void genFloorObjects(World world, BlockPos pos, Random random)
	{
		Block ground = world.getBlockState(pos).getBlock();
		if (ConfigLoader.isTerrain(ground) || ground == Blocks.SOUL_SAND)
		{
			boolean reeds = false;
			if (BlocksRegister.BLOCK_NETHER_REED != Blocks.AIR && random.nextInt(4) == 0)
				reeds = StructureReeds.generate(world, pos, random);
			if (!reeds)
			{
				if (BNWorldGenerator.hasStalagnateGen && random.nextInt(32) == 0)
					BNWorldGenerator.stalagnateGen.generate(world, pos, random);
				else if (BNWorldGenerator.hasEggPlantGen && random.nextInt(96) == 0)
					BNWorldGenerator.eggPlantGen.generate(world, pos, random);
				else if (random.nextInt(5) == 0)
					world.setBlockState(pos.up(), BlocksRegister.BLOCK_JUNGLE_PLANT.getDefaultState());
			}
		}
	}

	@Override
	public void genWallObjects(World world, BlockPos origin, BlockPos pos, Random random)
	{
		if (random.nextFloat() <= plantDensity && BNWorldGenerator.hasLucisGen && random.nextInt(4) == 0 && ConfigLoader.isTerrain(world.getBlockState(origin).getBlock()))
			BNWorldGenerator.lucisGen.generate(world, pos, random);
	}

	@Override
	public void genCeilObjects(World world, BlockPos pos, Random random)
	{
		if (random.nextFloat() <= plantDensity && BNWorldGenerator.hasEyeGen && random.nextInt(8) == 0 && random.nextDouble() * 4D + 0.5 < getFeatureNoise(pos))
			BNWorldGenerator.eyeGen.generate(world, pos.down(), random);
	}

	@Override
	public void genSurfColumn(World world, BlockPos pos, Random random)
	{
		if (world.getBlockState(pos).getBlock() == Blocks.NETHERRACK)
			world.setBlockState(pos, BlocksRegister.BLOCK_JUNGLE_GRASS.getDefaultState());
	}
}
