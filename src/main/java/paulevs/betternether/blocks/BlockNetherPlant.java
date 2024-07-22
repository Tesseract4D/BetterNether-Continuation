package paulevs.betternether.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IShearable;
import paulevs.betternether.config.ConfigLoader;

import java.util.List;
import java.util.Random;

public class BlockNetherPlant extends BlockBush implements IShearable {
    protected static final AxisAlignedBB GRASS_AABB = new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 0.75, 0.75);

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Nether;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return GRASS_AABB;
    }

    @Override
    protected boolean canSustainBush(IBlockState state)
    {
        return ConfigLoader.isTerrain(state.getBlock());
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return NonNullList.withSize(1, new ItemStack(this, 1));
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 0;
    }

    @Override
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XZ;
    }
}
