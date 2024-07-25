package paulevs.betternether.biomes;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BlocksRegistry;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.structures.plants.StructureReeds;
import paulevs.betternether.world.BNWorldGenerator;

import java.util.Random;

public class NetherJungleEdge extends NetherBiomeJungle {
    public NetherJungleEdge(String name) {
        super(name);
    }

    public boolean isEdge() {
        return true;
    }

    @Override
    public void genFloorObjects(World world, BlockPos pos, Random random) {
        Block ground = world.getBlockState(pos).getBlock();
        if (ConfigLoader.isTerrain(ground)) {
            boolean reeds = false;
            if (random.nextInt(4) == 0)
                reeds = StructureReeds.generate(world, pos, random);
            if (!reeds) {
                if (BNWorldGenerator.hasStalagnateGen && random.nextInt(48) == 0)
                    BNWorldGenerator.stalagnateGen.generate(world, pos, random);
                else if (BNWorldGenerator.hasEggPlantGen && random.nextInt(128) == 0)
                    BNWorldGenerator.eggPlantGen.generate(world, pos, random);
                else if (random.nextInt(5) == 0)
                    world.setBlockState(pos.up(), BlocksRegistry.BLOCK_JUNGLE_PLANT.getDefaultState());
            }
        }
    }
}
