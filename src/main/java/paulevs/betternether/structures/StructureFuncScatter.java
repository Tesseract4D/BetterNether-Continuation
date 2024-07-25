package paulevs.betternether.structures;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public abstract class StructureFuncScatter implements IStructure {
    protected static final BlockPos.MutableBlockPos POS = new BlockPos.MutableBlockPos();

    final int distance;
    final int manDist;

    public StructureFuncScatter(int distance) {
        this.distance = distance;
        this.manDist = (int) Math.ceil(distance * 1.5);
    }

    @Override
    public void generate(World world, BlockPos pos, Random random) {
        if (isGround(world.getBlockState(pos.down())) && noObjNear(world, pos)) {
            grow(world, pos, random);
        }
    }

    public abstract void grow(World world, BlockPos pos, Random random);

    protected abstract boolean isStructure(IBlockState state);

    protected abstract boolean isGround(IBlockState state);

    private boolean noObjNear(World world, BlockPos pos) {
        int x1 = pos.getX() - distance;
        int z1 = pos.getZ() - distance;
        int x2 = pos.getX() + distance;
        int z2 = pos.getZ() + distance;
        POS.y = pos.getY();
        for (int x = x1; x <= x2; x++) {
            POS.x = x;
            for (int z = z1; z <= z2; z++) {
                POS.z = z;
                if (isInside(x - pos.getX(), z - pos.getZ()) && isStructure(world.getBlockState(POS)))
                    return false;
            }
        }
        return true;
    }

    private boolean isInside(int x, int z) {
        return (Math.abs(x) + Math.abs(z)) <= manDist;
    }
}
