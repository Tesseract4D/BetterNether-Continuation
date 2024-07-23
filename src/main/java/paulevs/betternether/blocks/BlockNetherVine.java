package paulevs.betternether.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulevs.betternether.config.ConfigLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static net.minecraftforge.common.ForgeHooks.onCropsGrowPost;
import static net.minecraftforge.common.ForgeHooks.onCropsGrowPre;

public class BlockNetherVine extends Block implements IShearable {
    private static final AxisAlignedBB SELECT_AABB = new AxisAlignedBB(0.25, 0, 0.25, 0.75, 1, 0.75);
    public static final PropertyEnum<BlockNetherVine.EnumShape> SHAPE = PropertyEnum.create("shape", BlockNetherVine.EnumShape.class);

    public BlockNetherVine(Material m, MapColor c) {
        super(m, c);
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return NonNullList.withSize(1, new ItemStack(this, 1));
    }

    public enum EnumShape implements IStringSerializable {
        NORMAL(0, "normal"),
        BOTTOM(1, "bottom");

        private final String name;
        private final int index;

        EnumShape(int index, String name) {
            this.name = name;
            this.index = index;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public int getIndex() {
            return this.index;
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        IBlockState state = worldIn.getBlockState(pos.up());
        Block block = state.getBlock();
        return state.isOpaqueCube() || block == this;
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(SHAPE, meta == 1 ? BlockNetherVine.EnumShape.NORMAL : BlockNetherVine.EnumShape.BOTTOM);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(SHAPE).getIndex();
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SHAPE);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (this.canPlaceBlockAt(worldIn, pos)) {
            if (worldIn.isAirBlock(pos.down())) {
                if (onCropsGrowPre(worldIn, pos, state, true)) {
                    if (rand.nextInt(16) == 0)
                        worldIn.setBlockState(pos.down(), this.getDefaultState().withProperty(SHAPE, EnumShape.BOTTOM));
                    onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
                }
            }
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SELECT_AABB;
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        worldIn.setBlockState(pos, state.withProperty(SHAPE, worldIn.getBlockState(pos.down()).getBlock() == this ? EnumShape.NORMAL : EnumShape.BOTTOM));
        Block up = worldIn.getBlockState(pos.up()).getBlock();
        if (up != this && !ConfigLoader.isTerrain(up)) {
            worldIn.destroyBlock(pos, false);
            worldIn.scheduleBlockUpdate(pos.down(), Blocks.AIR, 0, 0);
        }
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
