package paulevs.betternether.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import paulevs.betternether.config.ConfigLoader;

import javax.annotation.Nullable;

public class BlockNetherBush extends BlockBush {
    protected static final AxisAlignedBB BUSH_AABB = new AxisAlignedBB(0.1, 0, 0.1, 0.9, 0.8, 0.9);

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().isReplaceable(world, pos) && ConfigLoader.isTerrain(world.getBlockState(pos.down()).getBlock());
    }

    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        return this.canSustain(world.getBlockState(pos.down()).getBlock());
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BUSH_AABB;
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    public boolean canSustain(Block p) {
        return ConfigLoader.isTerrain(p);
    }
}
