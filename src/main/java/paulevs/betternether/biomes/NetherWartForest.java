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
import paulevs.betternether.world.BNWorldGenerator;

import java.util.Random;

public class NetherWartForest extends NetherBiome
{
	public NetherWartForest(String name)
	{
		super(name);
		setFogColor(new Vec3d(0.59,0.02,0.02));
	}

	@Override
	public void genFloorObjects(World world, BlockPos pos, Random random)
	{
		Block d = world.getBlockState(pos).getBlock();
		if (random.nextFloat() <= plantDensity && ConfigLoader.isTerrain(d))
			{
				if (BNWorldGenerator.hasWartTreeGen && random.nextInt(30) == 0)
				{
					BNWorldGenerator.wartTreeGen.generate(world, pos, random);
				} else if (BNWorldGenerator.hasWartsGen && random.nextInt(12) == 0 && ConfigLoader.isTerrain(d) && world.getBlockState(pos.up()).getBlock() == Blocks.AIR)
					world.setBlockState(pos.up(), BlocksRegistry.BLOCK_WART_SEED.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.UP));
		}
	}
	
	@Override
	public void genSurfColumn(World world, BlockPos pos, Random random)
	{
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
