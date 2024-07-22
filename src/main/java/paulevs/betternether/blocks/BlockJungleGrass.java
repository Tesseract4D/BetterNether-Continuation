package paulevs.betternether.blocks;

import net.minecraft.block.BlockNetherrack;
import paulevs.betternether.BetterNether;

public class BlockJungleGrass extends BlockNetherrack
{
    public BlockJungleGrass()
    {
        this.setRegistryName("jungle_grass");
        this.setTranslationKey("jungle_grass");
        this.setCreativeTab(BetterNether.BN_TAB);
        this.setHardness(0.5F);
    }
}
