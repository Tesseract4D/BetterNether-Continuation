package paulevs.betternether.config;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import paulevs.betternether.biomes.BiomeRegister;
import paulevs.betternether.biomes.NetherBiome;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.items.ItemsRegister;
import paulevs.betternether.world.BNWorldGenerator;

public class ConfigLoader
{
	private static Configuration config;
	
	private static boolean[] registerBiomes;
	//private static boolean[] registerBlocks;
	private static boolean[] registerItems;
	private static int[] biomeWeights;
	
	private static int indexBiome;
	//private static int indexBlock;
	private static int indexItems;
	
	private static int biomeSizeXZ;
	private static int biomeSizeY;
	
	private static Map<String, Boolean> registerBlocks;
	
	private static boolean hasCleaningPass;
	private static boolean hasNetherWart;
	
	private static int cityDistance;
	private static boolean hasCities;
	private static String[] centers;
	private static String[] buildings;
	
	public static void load(File file)
	{
		List<Boolean> items= new ArrayList<Boolean>();
		config = new Configuration(file);
		config.load();
		biomeSizeXZ = config.getInt("BiomeSizeXZ", "Generator", 100, 1, 4096, "Defines size in horisontal space");
		biomeSizeY = config.getInt("BiomeSizeY", "Generator", 32, 1, 4096, "Defines size in vertical space");
		hasCleaningPass = config.getBoolean("SecondPass", "Generator", true, "Enables|Disables second pass for smooth terrain");
		hasNetherWart = config.getBoolean("NetherWartGeneration", "Generator", true, "Enables|Disables vanilla nether wart generation in biomes");
		cityDistance = config.getInt("CityGridSize", "Cities", 80, 8, 2048, "City grid size in chunks");
		hasCities = config.getBoolean("CityEnabled", "Cities", true, "Enables|Disables cities");
		centers = config.getStringList("CityCenters", "Cities", new String[]{"city_center_01", "city_center_02"}, "List of structures to use as city centers");
		buildings = config.getStringList("CityBuildings", "Cities", new String[]{"city_building_01", "city_building_02", "city_building_03", "city_building_04", "city_building_05", "city_building_06", "city_building_07", "city_building_08", "city_building_09", "city_building_10", "city_library_01", "city_tower_01", "city_tower_02", "city_enchanter_01", "city_hall"}, "List of structures to use as city buildings");
		 
		IntList weights = new IntArrayList();
		for (Field f : BiomeRegister.class.getDeclaredFields()) {
			if (f.getType().isAssignableFrom(NetherBiome.class)) {
				if(!f.getName().toLowerCase(Locale.ROOT).contains("_edge")) {
					if(!f.getName().equals("BIOME_BONE_REEF") && !f.getName().equals("BIOME_POOR_GRASSLANDS")) {
						weights.add(config.getInt(f.getName().toLowerCase(Locale.ROOT) + "_weight", "Biomes", 1, 1, Short.MAX_VALUE, "Biome weight (higher = more common)"));
					} else {
						weights.add(config.getInt(f.getName().toLowerCase(Locale.ROOT) + "_chance", "Biomes", 1, 1, Short.MAX_VALUE, "Chance in 1000 for this sub-biome to appear within the main biome"));
					}
				} else {
					weights.add(-1); // spacer
				}
				items.add(config.getBoolean(f.getName().toLowerCase(Locale.ROOT), "Biomes", true, "Enables|Disables biome"));
			}
		}
		registerBiomes = new boolean[items.size()];
		for (int i = 0; i < items.size(); i++)
			registerBiomes[i] = items.get(i);
		biomeWeights = new int[weights.size()];
		weights.toArray(biomeWeights);
		items.clear();

		registerBlocks = new HashMap<String, Boolean>();
		for (Field f : BlocksRegister.class.getDeclaredFields())
			if (f.getType().isAssignableFrom(Block.class))
				registerBlocks.put(f.getName().toLowerCase(Locale.ROOT), config.getBoolean(f.getName().toLowerCase(Locale.ROOT), "Blocks", true, "Enables|Disables block"));
		
		for (Field f : ItemsRegister.class.getDeclaredFields())
			if (f.getType().isAssignableFrom(Item.class))
				items.add(config.getBoolean(f.getName().toLowerCase(Locale.ROOT), "Items", true, "Enables|Disables item"));
		registerItems = new boolean[items.size()];
		for (int i = 0; i < items.size(); i++)
			registerItems[i] = items.get(i);
		items.clear();
		items = null;
		
		BNWorldGenerator.enablePlayerDamage = config.getBoolean("DamagePlayer", "EggplantDamage", true, "Damage for players");
		BNWorldGenerator.enableMobDamage = config.getBoolean("DamageMobs", "EggplantDamage", true, "Damage for mobs");
		
		resetBiomeIndex();
		resetItemIndex();
	}
	
	public static void postBiomeInit()
	{
		BNWorldGenerator.setPlantDensity(config.getFloat("GlobalDensity", "Generator", 1, 0, 1, "Global plant density, multiplied on other"));
		BNWorldGenerator.setStructureDensity(config.getFloat("StructureDensity", "Generator", 1F / 16F, 0, 1, "Structure density for random world structures"));
		BNWorldGenerator.setOreDensity(config.getFloat("OreDensity", "Generator", 1F / 1024F, 0, 1, "Cincinnasite ore density"));
		for (NetherBiome biome : BiomeRegister.getBiomes())
		{
			biome.setDensity(config.getFloat(biome.getName().replace(" ", "") + "Density", "Generator", 1, 0, 1, "Density for " + biome.getName() + " biome"));
		}
	}
	
	public static boolean mustInitBiome()
	{
		return registerBiomes[indexBiome++];
	}
	
	public static void resetBiomeIndex()
	{
		indexBiome = 0;
	}
	
	public static boolean mustInitBlock(Field field)
	{
		String s = field.getName().toLowerCase(Locale.ROOT);
		return registerBlocks.containsKey(s) && registerBlocks.get(s);
	}
	
	public static boolean mustInitBlock(String key)
	{
		String s = key.toLowerCase(Locale.ROOT);
		return registerBlocks.containsKey(s) && registerBlocks.get(s);
	}
	
	public static int biomeWeight() {
		return biomeWeights[indexBiome - 1];
	}
	
	/*public static void resetBlockIndex()
	{
		indexBlock = 0;
	}*/
	
	public static boolean mustInitItem()
	{
		return registerItems[indexItems++];
	}
	
	public static void resetItemIndex()
	{
		indexItems = 0;
	}
	
	public static void dispose()
	{
		config.save();
		registerBlocks = null;
		registerBiomes = null;
		registerItems = null;
	}
	
	public static int getBiomeSizeXZ()
	{
		return biomeSizeXZ;
	}
	
	public static int getBiomeSizeY()
	{
		return biomeSizeY;
	}
	
	public static boolean hasCleaningPass()
	{
		return hasCleaningPass;
	}
	
	public static boolean hasNetherWart()
	{
		return hasNetherWart;
	}
	
	public static int getCityDistance()
	{
		return cityDistance;
	}
	
	public static boolean hasCities()
	{
		return hasCities;
	}
	
	public static String[] getCityCenters() {
		return centers;
	}
	
	public static String[] getCityBuildings() {
		return buildings;
	}
}
