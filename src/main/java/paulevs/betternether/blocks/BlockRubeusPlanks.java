package paulevs.betternether.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import paulevs.betternether.BetterNether;

public class BlockRubeusPlanks extends Block {
    public BlockRubeusPlanks()
    {
        super(Material.WOOD, MapColor.RED_STAINED_HARDENED_CLAY);
        this.setHardness(2.0F);
        this.setSoundType(SoundType.WOOD);
        this.setTranslationKey("rubeus_planks");
        this.setRegistryName("rubeus_planks");
        this.setCreativeTab(BetterNether.BN_TAB);
    }
}
