package paulevs.betternether.blocks;

import net.minecraft.block.SoundType;
import paulevs.betternether.BetterNether;

public class BlockJunglePlant extends BlockNetherPlant{
    public BlockJunglePlant()
    {
        super();
        this.setRegistryName("jungle_plant");
        this.setTranslationKey("jungle_plant");
        this.setCreativeTab(BetterNether.BN_TAB);
        this.setSoundType(SoundType.PLANT);
    }
}
