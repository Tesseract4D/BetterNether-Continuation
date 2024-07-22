package paulevs.betternether;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import paulevs.betternether.betternether.Tags;
import paulevs.betternether.proxy.CommonProxy;
import paulevs.betternether.tab.BNCreativeTab;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies = "after:nether_api@[,INCOMPATIBLE WITH NETHER API)")
public class BetterNether
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
	}

	public static BetterNether getMod()
	{
		return mod;
	}
}
