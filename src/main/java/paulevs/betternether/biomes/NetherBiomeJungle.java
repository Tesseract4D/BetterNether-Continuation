package paulevs.betternether.biomes;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegistry;
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
		if (ConfigLoader.isTerrain(ground))
		{
			boolean reeds = false;
			if (random.nextInt(4) == 0)
				reeds = StructureReeds.generate(world, pos, random);
			if (!reeds)
			{
				if (random.nextInt(12) == 0)
					BNWorldGenerator.rubeusBushGen.generate(world, pos.up(), random);
				else if (random.nextInt(8) == 0)
					BNWorldGenerator.rubeusGen.generate(world, pos, random);
				else if (BNWorldGenerator.hasStalagnateGen && random.nextInt(32) == 0)
					BNWorldGenerator.stalagnateGen.generate(world, pos, random);
				else if (BNWorldGenerator.hasEggPlantGen && random.nextInt(96) == 0)
					BNWorldGenerator.eggPlantGen.generate(world, pos, random);
				else if (random.nextInt(5) == 0)
					world.setBlockState(pos.up(), BlocksRegistry.BLOCK_JUNGLE_PLANT.getDefaultState());
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
		if (random.nextFloat() <= plantDensity) {
			double noise = getFeatureNoise(pos);
			if (noise < -1.5 && random.nextInt(24) == 0)
			BNWorldGenerator.eyeGen.generate(world, pos.down(), random);
			else if (noise < -0.5 && random.nextInt(24) == 0)
				BNWorldGenerator.bloomingVineGen.generate(world, pos.down(), random);
			else if (noise < 1.5 && random.nextInt(24) == 0)
				BNWorldGenerator.blackVineGen.generate(world, pos.down(), random);
			else if (noise < 2.5 && random.nextInt(24) == 0)
				BNWorldGenerator.goldenVineGen.generate(world, pos.down(), random);
		}
	}

	@Override
	public void genSurfColumn(World world, BlockPos pos, Random random)
	{
		if (world.getBlockState(pos).getBlock() == Blocks.NETHERRACK)
			world.setBlockState(pos, BlocksRegistry.BLOCK_JUNGLE_GRASS.getDefaultState());
	}
}
