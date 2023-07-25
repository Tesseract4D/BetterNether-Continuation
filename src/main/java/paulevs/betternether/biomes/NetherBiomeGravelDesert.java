package paulevs.betternether.biomes;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlockNetherCactus;
import paulevs.betternether.blocks.BlocksRegister;

public class NetherBiomeGravelDesert extends NetherBiome {
	public NetherBiomeGravelDesert(String name) {
		super(name);
	}

	public int getDefaultWeight() {
		return 1000;
	}

	@Override
	public void genSurfColumn(World world, BlockPos pos, Random random) {
		// Only generate gravel sparingly (20% of the time)
		if (random.nextFloat() < 0.2f) {
			int clusterSize = random.nextInt(5) + 5;
			for (int i = 0; i < clusterSize; i++) {
				// Only generate the gravel after a certain Y height (Y > 25)
				if (pos.getY() > 25) {
					BlockPos p2 = pos.down(i);
					if (p2.getY() > -1 && world.getBlockState(p2).getBlock() == Blocks.NETHERRACK) {
						if (world.getBlockState(p2.down()).getBlock() == Blocks.AIR) {
							world.setBlockState(p2, Blocks.NETHERRACK.getDefaultState());
						} else {
							// Generate gravel in clusters
							world.setBlockState(p2, Blocks.GRAVEL.getDefaultState());
						}
					}
				}
			}
		}
	}

	@Override
	public void genFloorObjects(World world, BlockPos pos, Random random) {
		if (random.nextFloat() <= plantDensity && world.getBlockState(pos).getBlock() == Blocks.GRAVEL && ((random.nextInt(16) == 0 || getFeatureNoise(pos) > 0.3))) {
			if (BlocksRegister.BLOCK_NETHER_CACTUS != Blocks.AIR && random.nextInt(8) == 0) {
				int h = 1 + random.nextInt(3);
				for (int i = 1; i < h; i++) {
					if (world.getBlockState(pos.up(i)).getBlock() != Blocks.AIR) {
						h = i;
						break;
					}
				}
				for (int i = 1; i < h; i++) {
					world.setBlockState(pos.up(i), BlocksRegister.BLOCK_NETHER_CACTUS.getDefaultState().withProperty(BlockNetherCactus.SHAPE, BlockNetherCactus.EnumShape.SIDE));
				}
				world.setBlockState(pos.up(h), BlocksRegister.BLOCK_NETHER_CACTUS.getDefaultState());
			} else if (random.nextInt(7) == 0) {
				if (BlocksRegister.BLOCK_BARREL_CACTUS != Blocks.AIR && random.nextBoolean()) {
					world.setBlockState(pos.up(), BlocksRegister.BLOCK_BARREL_CACTUS.getDefaultState());
				} else if (BlocksRegister.BLOCK_AGAVE != Blocks.AIR) {
					world.setBlockState(pos.up(), BlocksRegister.BLOCK_AGAVE.getDefaultState());
				}
			}
		}
	}
}
