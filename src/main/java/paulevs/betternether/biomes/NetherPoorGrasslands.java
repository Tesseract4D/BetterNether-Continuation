package paulevs.betternether.biomes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.structures.plants.StructureReeds;
import paulevs.betternether.world.BNWorldGenerator;

import java.util.Random;

public class NetherPoorGrasslands extends NetherGrasslands
{
	public NetherPoorGrasslands(String name)
	{
		super(name);
	}
	
	public boolean isSub() {
		return true;
	}
	public int getDefaultWeight() {
		return 125;
	}
	
	@Override
	public void genFloorObjects(World world, BlockPos pos, Random random)
	{
		Block ground = world.getBlockState(pos).getBlock();
		if (random.nextFloat() <= plantDensity && ground instanceof BlockNetherrack || ground == Blocks.SOUL_SAND)
		{
			boolean reeds = false;
			if ( random.nextInt(4) == 0)
				reeds = StructureReeds.generate(world, pos, random);
			if (!reeds && random.nextFloat() < 0.4F) {
				if (BNWorldGenerator.hasSmokerGen && random.nextInt(64) == 0)
					BNWorldGenerator.smokerGen.generate(world, pos, random);
				else if (BNWorldGenerator.hasInkBushGen && random.nextInt(64) == 0)
					BNWorldGenerator.inkBushGen.generate(world, pos, random);
				else if (BNWorldGenerator.hasBlackAppleGen && random.nextInt(128) == 0)
					BNWorldGenerator.blackAppleGen.generate(world, pos, random);
				else if (random.nextInt(32) == 0 && getFeatureNoise(pos) > 0.3)
					world.setBlockState(pos.up(), BlocksRegister.BLOCK_BLACK_BUSH.getDefaultState());
				else if (random.nextInt(20) == 0)
					world.setBlockState(pos.up(), BlocksRegister.BLOCK_WART_SEED.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.UP));
				else if (random.nextInt(4) == 0)
					world.setBlockState(pos.up(), BlocksRegister.BLOCK_NETHER_GRASS.getDefaultState());
			}
		}
	}
}
