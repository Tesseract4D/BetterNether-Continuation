package paulevs.betternether.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import paulevs.betternether.BetterNether;

public class BlockGoldenVine extends BlockNetherVine {
    public BlockGoldenVine() {
        super(Material.PLANTS, MapColor.YELLOW);
        this.setRegistryName("golden_vine");
        this.setTranslationKey("golden_vine");
        this.setSoundType(SoundType.PLANT);
        this.setTickRandomly(true);
        this.setCreativeTab(BetterNether.BN_TAB);
    }
}
