package paulevs.betternether.structures.plants;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.blocks.BlockRubeusLog;
import paulevs.betternether.blocks.BlocksRegistry;
import paulevs.betternether.structures.IStructure;

import java.util.Random;

import static net.minecraft.block.BlockLeaves.CHECK_DECAY;

public class StructureRubeusBush implements IStructure {
    protected static final BlockPos.MutableBlockPos POS = new BlockPos.MutableBlockPos();

    @Override
    public void generate(World world, BlockPos pos, Random random) {
        if (!(world.isAirBlock(pos) && world.isAirBlock(pos.up()) && world.isAirBlock(pos.up(15))))
            return;

        float r = random.nextFloat() * 3 + 1;
        int count = (int) r;

        for (int i = 0; i < count; i++) {
            float fr = r - i;
            int ir = (int) Math.ceil(fr);
            float r2 = fr * fr;

            int x1 = pos.getX() - ir;
            int x2 = pos.getX() + ir;
            int z1 = pos.getZ() - ir;
            int z2 = pos.getZ() + ir;

            POS.setY(pos.getY() + i);

            for (int x = x1; x < x2; x++) {
                POS.x = (x);
                int sqx = x - pos.getX();
                sqx *= sqx;
                for (int z = z1; z < z2; z++) {
                    int sqz = z - pos.getZ();
                    sqz *= sqz;
                    POS.z = (z);
                    if (sqx + sqz < r2 + random.nextFloat() * r) {
                        setIfAir(world, POS, BlocksRegistry.BLOCK_RUBEUS_LEAVES.getDefaultState().withProperty(CHECK_DECAY, false));
                    }
                }
            }
        }

        BlocksHelper.setWithoutUpdate(world, pos, BlocksRegistry.BLOCK_RUBEUS_LOG.getDefaultState().withProperty(BlockRubeusLog.VARIANT, BlockRubeusLog.EnumType.MIDDLE));
        setIfAir(world, pos.up(), BlocksRegistry.BLOCK_RUBEUS_LEAVES.getDefaultState());
        setIfAir(world, pos.north(), BlocksRegistry.BLOCK_RUBEUS_LEAVES.getDefaultState());
        setIfAir(world, pos.south(), BlocksRegistry.BLOCK_RUBEUS_LEAVES.getDefaultState());
        setIfAir(world, pos.east(), BlocksRegistry.BLOCK_RUBEUS_LEAVES.getDefaultState());
        setIfAir(world, pos.west(), BlocksRegistry.BLOCK_RUBEUS_LEAVES.getDefaultState());
    }

    private void setIfAir(World world, BlockPos pos, IBlockState state) {
        if (world.isAirBlock(pos))
            BlocksHelper.setWithoutUpdate(world, pos, state);
    }
}
