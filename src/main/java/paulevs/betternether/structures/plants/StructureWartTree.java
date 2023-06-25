package paulevs.betternether.structures.plants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.structures.IStructure;

public class StructureWartTree implements IStructure {
	private static final IBlockState WART = Blocks.NETHER_WART_BLOCK.getDefaultState();

	@Override
	public void generate(World world, BlockPos pos, Random random) {
		int height = 5 + random.nextInt(5);

		boolean b1 = world.getBlockState(new BlockPos(pos.getX() - 4, pos.getY() + 5, pos.getZ())).getBlock() == Blocks.AIR;
		boolean b2 = world.getBlockState(new BlockPos(pos.getX() + 4, pos.getY() + 5, pos.getZ())).getBlock() == Blocks.AIR;
		boolean b3 = world.getBlockState(new BlockPos(pos.getX(), pos.getY() + 5, pos.getZ() - 4)).getBlock() == Blocks.AIR;
		boolean b4 = world.getBlockState(new BlockPos(pos.getX(), pos.getY() + 5, pos.getZ() + 4)).getBlock() == Blocks.AIR;
		boolean b5 = world.getBlockState(new BlockPos(pos.getX(), pos.getY() + height, pos.getZ())).getBlock() == Blocks.AIR;

		if (b1 && b2 && b3 && b4 && b5) {
			int h2 = height >>> 1;
			int width = (height >>> 2) + 1;
			int offset = width >>> 1;
			List<BlockPos> positions = new ArrayList<>();
			for (int x = 0; x < width; x++) {
				int px = x + pos.getX() - offset;
				for (int z = 0; z < width; z++) {
					int pz = z + pos.getZ() - offset;
					int rh = random.nextInt(h2);
					for (int y = 0; y < height; y++) {
						int py = y + pos.getY();
						BlockPos position = new BlockPos(px, py, pz);
						if (y < rh && random.nextInt(2) == 0) {
							positions.add(position);
						} else {
							positions.add(position);
							EnumFacing dir = EnumFacing.HORIZONTALS[random.nextInt(4)];
							positions.add(position.offset(dir));
						}
					}
				}
			}

			world.captureBlockSnapshots = true;

			for (BlockPos position : positions) {
				world.setBlockState(position, WART);
			}

			world.captureBlockSnapshots = false;
			world.capturedBlockSnapshots.clear();

			int headWidth = width + 2;
			offset++;
			height = height - width - 1 + pos.getY();
			for (int x = 0; x < headWidth; x++) {
				int px = x + pos.getX() - offset;
				for (int z = 0; z < headWidth; z++) {
					if (x != z && x != (headWidth - z - 1)) {
						int pz = z + pos.getZ() - offset;
						for (int y = 0; y < width; y++) {
							int py = y + height;
							BlockPos position = new BlockPos(px, py, pz);
							if (world.getBlockState(position) != WART) {
								world.setBlockState(position, WART);
							}
						}
					}
				}
			}

			for (BlockPos position : positions) {
				placeRandomSeed(world, position);
			}
		}
	}

	private void placeRandomSeed(World world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock() == Blocks.AIR) {
			if (BlocksRegister.BLOCK_WART_SEED == Blocks.AIR) {
				return;
			}
			IBlockState seed = BlocksRegister.BLOCK_WART_SEED.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.UP);
			if (world.getBlockState(pos.north()).isFullBlock()) {
				seed = seed.withProperty(BlockDirectional.FACING, EnumFacing.SOUTH);
			} else if (world.getBlockState(pos.south()).isFullBlock()) {
				seed = seed.withProperty(BlockDirectional.FACING, EnumFacing.NORTH);
			} else if (world.getBlockState(pos.east()).isFullBlock()) {
				seed = seed.withProperty(BlockDirectional.FACING, EnumFacing.WEST);
			} else if (world.getBlockState(pos.west()).isFullBlock()) {
				seed = seed.withProperty(BlockDirectional.FACING, EnumFacing.EAST);
			} else if (world.getBlockState(pos.up()).isFullBlock()) {
				seed = seed.withProperty(BlockDirectional.FACING, EnumFacing.DOWN);
			}
			world.setBlockState(pos, seed);
		}
	}
}