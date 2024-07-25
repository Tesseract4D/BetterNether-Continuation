package paulevs.betternether.biomes;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlockNetherCactus;
import paulevs.betternether.blocks.BlocksRegistry;

import java.util.Random;

public class NetherBiomeGravelDesert extends NetherBiome {
	public NetherBiomeGravelDesert(String name) {
		super(name);
		setFogColor(new Vec3d(0.66, 0.19, 0));
	}

	@Override
	public void genSurfColumn(World world, BlockPos pos, Random random) {
		int clusterSize = random.nextInt(3) + 1;
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

	@Override
	public void genFloorObjects(World world, BlockPos pos, Random random) {
		if (random.nextFloat() <= plantDensity && world.getBlockState(pos).getBlock() == Blocks.GRAVEL && ((random.nextInt(16) == 0 || getFeatureNoise(pos) > 0.3))) {
			if (random.nextInt(24) == 0) {
				world.setBlockState(pos.up(), BlocksRegistry.BLOCK_AGAVE.getDefaultState());
			} else if (random.nextInt(75) == 0) {
				int h = 2 + random.nextInt(3);
				for (int i = 1; i < h; i++) {
					if (world.getBlockState(pos.up(i)).getBlock() != Blocks.AIR) {
						h = i;
						break;
					}
				}
				for (int i = 1; i < h; i++) {
					world.setBlockState(pos.up(i), BlocksRegistry.BLOCK_NETHER_CACTUS.getDefaultState().withProperty(BlockNetherCactus.SHAPE, BlockNetherCactus.EnumShape.SIDE));
				}
				world.setBlockState(pos.up(h), BlocksRegistry.BLOCK_NETHER_CACTUS.getDefaultState());
			} else if (random.nextInt(75) == 0) {
				world.setBlockState(pos.up(), BlocksRegistry.BLOCK_BARREL_CACTUS.getDefaultState());
			}
		}
	}
}
