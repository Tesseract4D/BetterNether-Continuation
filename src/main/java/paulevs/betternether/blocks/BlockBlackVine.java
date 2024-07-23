package paulevs.betternether.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import paulevs.betternether.BetterNether;

public class BlockBlackVine extends BlockNetherVine {
    public BlockBlackVine() {
        super(Material.PLANTS, MapColor.BLACK);
        this.setRegistryName("black_vine");
        this.setTranslationKey("black_vine");
        this.setSoundType(SoundType.PLANT);
        this.setTickRandomly(true);
        this.setCreativeTab(BetterNether.BN_TAB);
    }
}
