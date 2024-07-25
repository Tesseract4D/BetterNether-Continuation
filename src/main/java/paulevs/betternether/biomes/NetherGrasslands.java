package paulevs.betternether.biomes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegistry;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.structures.plants.StructureReeds;
import paulevs.betternether.world.BNWorldGenerator;

import java.util.Random;

public class NetherGrasslands extends NetherBiome
{
	public NetherGrasslands(String name)
	{
		super(name);
		setFogColor(new Vec3d(0.44, 0.28, 0.52));
	}

	@Override
	public void genFloorObjects(World world, BlockPos pos, Random random)
	{
		Block ground = world.getBlockState(pos).getBlock();
		if (random.nextFloat() <= plantDensity && ConfigLoader.isGenTerrain(ground))
		{
			boolean reeds = false;
			if (random.nextInt(4) == 0)
				reeds = StructureReeds.generate(world, pos, random);
			if (!reeds) {
				if (BNWorldGenerator.hasSmokerGen && random.nextInt(64) == 0)
					BNWorldGenerator.smokerGen.generate(world, pos, random);
				else if (BNWorldGenerator.hasInkBushGen && random.nextInt(64) == 0)
					BNWorldGenerator.inkBushGen.generate(world, pos, random);
				else if (BNWorldGenerator.hasBlackAppleGen && random.nextInt(128) == 0)
					BNWorldGenerator.blackAppleGen.generate(world, pos, random);
				else if (random.nextInt(32) == 0 && getFeatureNoise(pos) > 0.3)
					world.setBlockState(pos.up(), BlocksRegistry.BLOCK_BLACK_BUSH.getDefaultState());
				else if (random.nextInt(20) == 0)
					world.setBlockState(pos.up(), BlocksRegistry.BLOCK_WART_SEED.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.UP));
				else if (random.nextInt(4) == 0)
					world.setBlockState(pos.up(), BlocksRegistry.BLOCK_NETHER_GRASS.getDefaultState());
			}
		}
	}

	@Override
	public void genSurfColumn(World world, BlockPos pos, Random random) {
		if (world.getBlockState(pos).getBlock() == Blocks.NETHERRACK)
			switch (random.nextInt(5)) {
				case 0:
					world.setBlockState(pos, BlocksRegistry.BLOCK_SOUL_SOIL.getDefaultState());
					break;
				case 1:
				case 2:
				case 3:
					world.setBlockState(pos, Blocks.SOUL_SAND.getDefaultState());
					break;
				default:
					world.setBlockState(pos, BlocksRegistry.BLOCK_NETHERRACK_MOSS.getDefaultState());
			}
	}
}
