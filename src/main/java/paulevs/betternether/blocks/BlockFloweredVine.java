package paulevs.betternether.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import paulevs.betternether.BetterNether;

public class BlockFloweredVine extends BlockNetherVine {
    public BlockFloweredVine() {
        super(Material.PLANTS, MapColor.YELLOW);
        this.setRegistryName("flowered_vine");
        this.setTranslationKey("flowered_vine");
        this.setSoundType(SoundType.PLANT);
        this.setTickRandomly(true);
        this.setCreativeTab(BetterNether.BN_TAB);
    }
}
