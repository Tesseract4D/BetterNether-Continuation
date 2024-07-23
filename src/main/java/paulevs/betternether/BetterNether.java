package paulevs.betternether;

import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.tclproject.mysteriumlib.asm.common.CustomLoadingPlugin;
import net.tclproject.mysteriumlib.asm.common.FirstClassTransformer;
import net.tclproject.mysteriumlib.asm.core.MetaReader;
import net.tclproject.mysteriumlib.asm.core.MiscUtils;
import paulevs.betternether.betternether.Tags;
import paulevs.betternether.proxy.CommonProxy;
import paulevs.betternether.tab.BNCreativeTab;

import java.io.IOException;
import java.util.List;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies = "after:nether_api@[,INCOMPATIBLE WITH NETHER API)")
public class BetterNether extends CustomLoadingPlugin
{
	public static final CreativeTabs BN_TAB = new BNCreativeTab();
	
	@SidedProxy(clientSide = "paulevs.betternether.proxy.ClientProxy", serverSide = "paulevs.betternether.proxy.CommonProxy")
	public static CommonProxy proxy;
	private static BetterNether mod;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		mod = this;
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
	    proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
	    proxy.postInit(event);
        try {
            System.out.println("@"+ new MetaReader().getLocalVariables(GuiOverlayDebug.class.getDeclaredMethod("call")));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	public static BetterNether getMod()
	{
		return mod;
	}

	public String[] getASMTransformerClass() {
		return new String[]{FirstClassTransformer.class.getName()};
	}

	public void registerFixes() {
		registerClassWithFixes("paulevs.betternether.fix.FixesCommon");
	}
}
