package paulevs.betternether.structures;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureBuilding extends StructureNBT implements IStructureWorld {
	private int offsetY;

	public StructureBuilding(String structure, int offsetY) {
		super(structure);
		this.offsetY = offsetY;
	}

	@Override
	public void generate(World world, BlockPos pos, Rotation rotation) {
		generateCentered(world, pos.add(0, offsetY, 0), rotation);
	}

	@Override
	public float getAirFraction(World world, BlockPos pos, Rotation rotation) {
		int airCount = 0;
		BlockPos size = getSize(rotation);
		BlockPos start = new BlockPos(size.getX() >> 1, size.getY() + offsetY, size.getZ() >> 1);
		IBlockState airState = Blocks.AIR.getDefaultState();

		for (int x = -start.getX(); x < size.getX() - start.getX(); x++) {
			for (int y = -start.getY(); y < size.getY(); y++) {
				for (int z = -start.getZ(); z < size.getZ() - start.getZ(); z++) {
					BlockPos currentPos = pos.add(x, y, z);
					IBlockState currentState = world.getBlockState(currentPos);
					if (currentState.getBlock() == airState) {
						airCount++;
					}
				}
			}
		}

		return (float) airCount / (float) (size.getX() * (size.getY() + offsetY) * size.getZ());
	}
}
