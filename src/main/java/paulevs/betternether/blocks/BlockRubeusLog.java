package paulevs.betternether.blocks;

import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import paulevs.betternether.BetterNether;

public class BlockRubeusLog extends BlockLog {
    public static final PropertyEnum<BlockRubeusLog.EnumType> VARIANT = PropertyEnum.create("variant", BlockRubeusLog.EnumType.class, a -> true);

    public BlockRubeusLog() {
        super();
        this.setRegistryName("rubeus_log");
        this.setTranslationKey("rubeus_log");
        this.setCreativeTab(BetterNether.BN_TAB);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumType.BOTTOM).withProperty(LOG_AXIS, EnumAxis.Y));
    }

    public IBlockState getStateFromMeta(int meta) {
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, EnumType.byIndex(meta & 3));

        switch (meta & 12) {
            case 0:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
                break;
            case 4:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
                break;
            case 8:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
                break;
            default:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
                break;
        }

        return iblockstate;
    }

    public int getMetaFromState(IBlockState state) {
        int i = state.getValue(VARIANT).getIndex();

        switch (state.getValue(LOG_AXIS)) {
            case X:
                i |= 4;
                break;
            case Z:
                i |= 8;
                break;
            case NONE:
                i |= 12;
        }

        return i;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT, LOG_AXIS);
    }

    public int damageDropped(IBlockState state) {
        return 0;
    }

    public enum EnumType implements IStringSerializable {
        TOP(2, "top"),
        MIDDLE(1, "middle"),
        BOTTOM(0, "bottom");

        private final String name;
        private final int index;

        EnumType(int index, String name) {
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

        public static EnumType byIndex(int i) {
            switch (i) {
                case 0:
                    return BOTTOM;
                case 1:
                    return MIDDLE;
                default:
                    return TOP;
            }
        }
    }
}
