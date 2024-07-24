package paulevs.betternether.blocks;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.BetterNether;

public class BlockRubeusLog extends BlockRotatedPillar {
    public static final PropertyEnum<BlockRubeusLog.EnumType> VARIANT = PropertyEnum.create("variant", BlockRubeusLog.EnumType.class);

    public BlockRubeusLog() {
        super(Material.WOOD, MapColor.RED_STAINED_HARDENED_CLAY);
        this.setRegistryName("rubeus_log");
        this.setTranslationKey("rubeus_log");
        this.setSoundType(SoundType.WOOD);
        this.setHardness(2.0F);
        this.setCreativeTab(BetterNether.BN_TAB);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumType.BOTTOM).withProperty(AXIS, EnumFacing.Axis.Y));
    }

    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, EnumType.TOP.getMetadata()));
        items.add(new ItemStack(this, 1, EnumType.MIDDLE.getMetadata()));
        items.add(new ItemStack(this, 1, EnumType.BOTTOM.getMetadata()));
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        System.out.println("&" + meta);
        System.out.println("&" + (meta & 3));
        System.out.println("&" + EnumType.byMetadata(meta & 3));
        return this.getStateFromMeta(meta).withProperty(AXIS, facing.getAxis());
    }

    public IBlockState getStateFromMeta(int meta) {
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta & 3));

        switch (meta & 12) {
            case 0:
                iblockstate = iblockstate.withProperty(AXIS, EnumFacing.Axis.Y);
                break;
            case 4:
                iblockstate = iblockstate.withProperty(AXIS, EnumFacing.Axis.X);
                break;
            case 8:
                iblockstate = iblockstate.withProperty(AXIS, EnumFacing.Axis.Z);
                break;
        }

        return iblockstate;
    }

    public int getMetaFromState(IBlockState state) {
        int i = state.getValue(VARIANT).getMetadata();

        switch (state.getValue(AXIS)) {
            case X:
                i |= 4;
                break;
            case Z:
                i |= 8;
                break;
        }

        return i;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT, AXIS);
    }

    protected ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(VARIANT).getMetadata());
    }

    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    public enum EnumType implements IStringSerializable {

        TOP(0, "top"),
        MIDDLE(1, "middle"),
        BOTTOM(2, "bottom");

        private static final EnumType[] METADATA_LOOKUP = new EnumType[values().length];
        public static final int length = values().length;
        private final int metadata;
        private final String name;

        EnumType(int metadataIn, String nameIn) {
            metadata = metadataIn;
            name = nameIn;
        }

        public int getMetadata() {
            return metadata;
        }

        public String toString() {
            return name;
        }

        public static EnumType byMetadata(int metadata) {
            if (metadata < 0 || metadata >= METADATA_LOOKUP.length) {
                metadata = 0;
            }

            return METADATA_LOOKUP[metadata];
        }

        public String getName() {
            return name;
        }

        static {
            for (EnumType v : values()) {
                METADATA_LOOKUP[v.getMetadata()] = v;
            }
        }
    }
}
