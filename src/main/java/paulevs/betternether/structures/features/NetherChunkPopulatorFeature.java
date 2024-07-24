package paulevs.betternether.structures.features;

import net.minecraft.util.math.BlockPos;
import paulevs.betternether.world.BNWorldGenerator;

import java.util.concurrent.atomic.AtomicInteger;

public class NetherChunkPopulatorFeature {
    public static final ThreadLocal<BNWorldGenerator> WORLD_GENERATOR = ThreadLocal.withInitial(() -> new BNWorldGenerator());
    public static void clearGeneratorPool(){}

    public static BNWorldGenerator generatorForThread() { return WORLD_GENERATOR.get(); }
    public static final AtomicInteger callCounter = new AtomicInteger();
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> featurePlaceContext) {
        final BlockPos worldPos = featurePlaceContext.origin();
        final WorldGenLevel level = featurePlaceContext.level();
        final int sx = (worldPos.getX() >> 4) << 4;
        final int sz = (worldPos.getZ() >> 4) << 4;

        BNWorldGenerator generator = generatorForThread();

        //BetterNether.LOGGER.info(" +++ Starting populate " + sx + "/" + sz + " on " + Thread.currentThread() + " (" + generatorPool.size()  +")");
        generator.prePopulate(level, sx, sz, featurePlaceContext);
        generator.populate(level, sx, sz, featurePlaceContext);
        //BetterNether.LOGGER.info(" --- Finished populate " + sx + "/" + sz);

        return true;
    }
}
