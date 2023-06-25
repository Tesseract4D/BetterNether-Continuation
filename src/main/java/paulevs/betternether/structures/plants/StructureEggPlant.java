package paulevs.betternether.structures.plants;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.structures.IStructure;

public class StructureEggPlant implements IStructure {
	private static final int OUTER_LOOP_ITERATIONS = 18;
	private static final int INNER_LOOP_ITERATIONS = 6;
	private static final int MIN_Y = 32;

	public void generate(World world, BlockPos pos, Random random) {
		Block under = world.getBlockState(pos).getBlock();
		if (ConfigLoader.isTerrain(under) || under == Blocks.SOUL_SAND) {
			IBlockState state = BlocksRegister.BLOCK_EGG_PLANT.getDefaultState();

			for (int i = 0; i < OUTER_LOOP_ITERATIONS; i++) {
				double gaussianX = random.nextGaussian() * 2;
				double gaussianZ = random.nextGaussian() * 2;
				int x = pos.getX() + (int) gaussianX;
				int z = pos.getZ() + (int) gaussianZ;
				int y = pos.getY() + random.nextInt(INNER_LOOP_ITERATIONS);

				BlockPos airPos = null;

				for (int j = 0; j < INNER_LOOP_ITERATIONS; j++) {
					// Find first potential air position
					BlockPos downNpos = new BlockPos(x, y - j, z).down();
					if (world.getBlockState(downNpos).getBlock() != Blocks.AIR) {
						airPos = new BlockPos(x, y - j, z);
						break;
					}
				}

				// Check if actual valid base
				if(airPos != null && world.getBlockState(airPos.down()).getBlock() != Blocks.AIR) {
					// Batch block state modifications
					IBlockState[] states = new IBlockState[INNER_LOOP_ITERATIONS];
					BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(airPos);
					for (int j = 0; j < INNER_LOOP_ITERATIONS; j++) {
						mutablePos.setY(airPos.getY() - j);
						states[j] = world.getBlockState(mutablePos);
					}
					for (int j = 0; j < INNER_LOOP_ITERATIONS; j++) {
						if (states[j].getMaterial().isReplaceable()) {
							mutablePos.setY(airPos.getY() - j);
							world.setBlockState(mutablePos, state);
						}
					}
					break;
				}
			}
		}
	}
}
