package paulevs.betternether.structures.plants;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.BlocksHelper;
import paulevs.betternether.blocks.BlockNetherVine;
import paulevs.betternether.structures.IStructure;

import java.util.Random;

public class StructureVine implements IStructure {

    private BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

    public final Block block;

    public StructureVine(Block block) {
        this.block = block;
    }

    @Override
    public void generate(World world, BlockPos pos, Random random) {
        int h = BlocksHelper.downRay(world, pos, 25);
        if (h < 2)
            return;
        h = random.nextInt(h) + 1;
        IBlockState bottom = block.getDefaultState().withProperty(BlockNetherVine.SHAPE, BlockNetherVine.EnumShape.BOTTOM);
        IBlockState middle = block.getDefaultState().withProperty(BlockNetherVine.SHAPE, BlockNetherVine.EnumShape.NORMAL);

        blockPos.setPos(pos);
        for (int y = 0; y < h; y++) {
            blockPos.setY(pos.getY() - y);
            if (world.isAirBlock(blockPos.down()))
                BlocksHelper.setWithoutUpdate(world, blockPos, middle);
            else {
                BlocksHelper.setWithoutUpdate(world, blockPos, bottom);
                return;
            }
        }
        BlocksHelper.setWithoutUpdate(world, blockPos.down(), bottom);
    }
}
