package paulevs.betternether.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import paulevs.betternether.BetterNether;

public class BlockSoulSoil extends Block {
    public BlockSoulSoil() {
        super(Material.GROUND, MapColor.BROWN);
        this.setRegistryName("soul_soil");
        this.setTranslationKey("soul_soil");
        this.setHardness(0.5F);
        this.setSoundType(SoundType.GROUND);
        this.setCreativeTab(BetterNether.BN_TAB);
    }
}
