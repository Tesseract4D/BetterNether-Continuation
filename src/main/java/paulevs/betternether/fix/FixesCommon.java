package paulevs.betternether.fix;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldProviderHell;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tclproject.mysteriumlib.asm.annotations.EnumReturnSetting;
import net.tclproject.mysteriumlib.asm.annotations.Fix;
import net.tclproject.mysteriumlib.asm.annotations.LocalVariable;
import paulevs.betternether.world.BNWorldGenerator;

import java.util.List;

public class FixesCommon {
    public static Vec3d lastFogColor = null;
    public static Vec3d fadingFog = null;
    public static int fogFadeTime = 0;

    @Fix(insertOnInvoke = "java/util/List;add(Ljava/lang/Object;)Z", insertOnLine = 0)
    public static void call(GuiOverlayDebug c, @LocalVariable(index = 1) BlockPos p, @LocalVariable(index = 5) List list) {
        if (c.mc.world.provider.getDimension() == -1)
            list.add("Nether Biome: " + BNWorldGenerator.getBiome(c.mc.world, p));
    }

    @SideOnly(Side.CLIENT)
    @Fix(returnSetting = EnumReturnSetting.ALWAYS, createNewMethod = true)
    public static Vec3d getFogColor(WorldProviderHell c, float f, float partialTicks) {
        if (fadingFog != null) {
            float s = fogFadeTime / 100f;
            return fadingFog.scale(s).add(lastFogColor.scale(1 - s));
        }
        Vec3d fog = BNWorldGenerator.getBiome(c.world, Minecraft.getMinecraft().player.getPosition()).getFogColor(f, partialTicks);
        if (lastFogColor != null && lastFogColor != fog) {
            fadingFog = new Vec3d(lastFogColor.x, lastFogColor.y, lastFogColor.z);
            fogFadeTime = 100;
            lastFogColor = fog;
            return fadingFog;
        }
        lastFogColor = fog;
        return fog;
    }
}
