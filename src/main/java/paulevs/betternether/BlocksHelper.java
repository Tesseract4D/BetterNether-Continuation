package paulevs.betternether;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.Properties;
import paulevs.betternether.blocks.BlocksRegistry;
import paulevs.betternether.config.ConfigLoader;

public class BlocksHelper {
    public static final int FLAG_UPDATE_BLOCK = 1;
    public static final int FLAG_SEND_CLIENT_CHANGES = 2;
    public static final int FLAG_NO_RERENDER = 4;
    public static final int FORSE_RERENDER = 8;
    public static final int FLAG_IGNORE_OBSERVERS = 16;

    public static final int SET_SILENT = FLAG_UPDATE_BLOCK | FLAG_IGNORE_OBSERVERS | FLAG_SEND_CLIENT_CHANGES;

    public static boolean isLava(IBlockState state) {
        return state.getMaterial() == Material.LAVA;
    }

    public static boolean isNetherrack(IBlockState state) {
        Block b = state.getBlock();
        return b == Blocks.NETHERRACK ||
                b == Blocks.QUARTZ_ORE ||
                b == BlocksRegistry.BLOCK_CINCINNASITE_ORE ||
                b == BlocksRegistry.BLOCK_NETHERRACK_MOSS;
    }

    public static boolean isSoulSand(IBlockState state) {
        Block b = state.getBlock();
        return b == Blocks.SOUL_SAND ||
                b == BlocksRegistry.BLOCK_SOUL_SOIL;
    }

    public static boolean isNetherGround(IBlockState state) {
        Block b = state.getBlock();
        return ConfigLoader.isTerrain(b);
    }

    public static boolean isNetherGroundMagma(IBlockState state) {
        Block b = state.getBlock();
        return isNetherGround(state) || b == Blocks.MAGMA;
    }

    public static boolean isBone(IBlockState state) {
        Block b = state.getBlock();
        return b == Blocks.BONE_BLOCK ||
                b == BlocksRegistry.BLOCK_BONE;
    }

    public static void setWithoutUpdate(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state, SET_SILENT);
    }

    public static int upRay(World world, BlockPos pos, int maxDist) {
        int length = 0;
        for (int j = 1; j < maxDist && (world.isAirBlock(pos.up(j))); j++)
            length++;
        return length;
    }

    public static int downRay(World world, BlockPos pos, int maxDist) {
        int length = 0;
        for (int j = 1; j < maxDist && (world.isAirBlock(pos.down(j))); j++)
            length++;
        return length;
    }

    public static IBlockState rotateHorizontal(IBlockState state, Rotation rotation, IProperty<EnumFacing> facing) {
        return state.withProperty(facing, rotation.rotate(state.getValue(facing)));
    }

    public static IBlockState mirrorHorizontal(IBlockState state, Mirror mirror, IProperty<EnumFacing> facing) {
        return state.withRotation(mirror.toRotation(state.getValue(facing)));
    }

    public static int getLengthDown(World world, BlockPos pos, Block block) {
        int count = 1;
        while (world.getBlockState(pos.down(count)).getBlock() == block)
            count++;
        return count;
    }

    public static boolean isFertile(IBlockState state) {
        return state.getBlock() instanceof BlockFarmland;
    }
}