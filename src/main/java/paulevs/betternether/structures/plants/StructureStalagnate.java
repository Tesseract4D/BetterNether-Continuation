package paulevs.betternether.structures.plants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.structures.IStructure;

public class StructureStalagnate implements IStructure {
	@Override
	public void generate(World world, BlockPos pos, Random random) {
		BlockPos up = upRay(world, pos);
		if (up != BlockPos.ORIGIN) {
			IBlockState bottomState = BlocksRegister.BLOCK_STALAGNATE_BOTTOM.getDefaultState();
			IBlockState middleState = BlocksRegister.BLOCK_STALAGNATE_MIDDLE.getDefaultState();
			IBlockState topState = BlocksRegister.BLOCK_STALAGNATE_TOP.getDefaultState();

			int minY = pos.getY() + 1;
			int maxY = up.getY() - 1;

			if (maxY - minY >= 1) {
				List<BlockPos> positions = new ArrayList<>();
				for (int y = minY; y <= maxY; y++) {
					positions.add(new BlockPos(pos.getX(), y, pos.getZ()));
				}

				world.captureBlockSnapshots = true;

				world.setBlockState(pos.up(), bottomState);
				world.setBlockState(up.down(), topState);

				for (BlockPos position : positions) {
					if (world.getBlockState(position).getBlock() == Blocks.AIR) {
						world.setBlockState(position, middleState);
					}
				}

				world.captureBlockSnapshots = false;
				world.capturedBlockSnapshots.clear();
			}
		}
	}

	private BlockPos upRay(World world, BlockPos start) {
		for (int j = start.getY() + 1; j <= world.getHeight(); j++) {
			BlockPos checkPos = new BlockPos(start.getX(), j, start.getZ());
			if (world.getBlockState(checkPos).getBlock() != Blocks.AIR) {
				// Check if block below is not air
				BlockPos checkBelow = checkPos.down();
				if (world.getBlockState(checkBelow).getBlock() != Blocks.AIR) {
					return checkPos;
				}
			}
		}
		return BlockPos.ORIGIN;
	}
}