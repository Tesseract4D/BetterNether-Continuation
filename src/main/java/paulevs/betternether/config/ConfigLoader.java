package paulevs.betternether.config;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import paulevs.betternether.biomes.BiomeRegister;
import paulevs.betternether.biomes.NetherBiome;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.items.ItemsRegister;
import paulevs.betternether.world.BNWorldGenerator;

public class ConfigLoader
{
	private static Configuration config;
	//private static boolean[] registerBlocks;
	private static boolean[] registerItems;
	
	private static Set<NetherBiome> enabledBiomes;
	private static List<NetherBiome> everywhereBiomes;
	private static Map<String, List<NetherBiome>> restrictedBiomes;
	
	private static final Set<Block> NETHER_TERRAIN = new HashSet<>();
	private static final Set<Block> NETHER_GEN_TERRAIN = new HashSet<>();
	private static final Set<Block> NETHER_GEN_REPL_TERRAIN = new HashSet<>();
	private static String[] netherTerrainIds;
	private static String[] netherGenTerrainIds;
	private static String[] netherGenReplTerrainIds;
	private static Path structureLoadPath;
	
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
	
	public static void load(File file, File configDir)
	{
		structureLoadPath = configDir.toPath().resolve("betternether").resolve("structures");
		List<Boolean> items= new ArrayList<Boolean>();
		config = new Configuration(file);
		config.load();
		biomeSizeXZ = config.getInt("BiomeSizeXZ", "Generator", 100, 1, 4096, "Defines size in horisontal space");
		biomeSizeY = config.getInt("BiomeSizeY", "Generator", 32, 1, 4096, "Defines size in vertical space");
		hasCleaningPass = config.getBoolean("SecondPass", "Generator", true, "Enables|Disables second pass for smooth terrain");
		hasNetherWart = config.getBoolean("NetherWartGeneration", "Generator", true, "Enables|Disables vanilla nether wart generation in biomes");
		cityDistance = config.getInt("CityGridSize", "Cities", 80, 8, 2048, "City grid size in chunks");
		hasCities = config.getBoolean("CityEnabled", "Cities", true, "Enables|Disables cities");
		centers = config.getStringList("CityCenters", "Cities", new String[]{"city_center_01", "city_center_02"}, "List of structures to use as city centers. Loaded from config/betternether/structures/city. (You can also override built-in ones by putting files with the same name there.)");
		buildings = config.getStringList("CityBuildings", "Cities", new String[]{"city_building_01", "city_building_02", "city_building_03", "city_building_04", "city_building_05", "city_building_06", "city_building_07", "city_building_08", "city_building_09", "city_building_10", "city_library_01", "city_tower_01", "city_tower_02", "city_enchanter_01", "city_hall"}, "List of structures to use as city buildings. Loaded from config/betternether/structures/city. (You can also override built-in ones by putting files with the same name there.)");
		netherTerrainIds = config.getStringList("PlantableBlocks", "Other", new String[]{"minecraft:netherrack", "betternether:nether_mycelium", "betternether:netherrack_moss"}, "List of blocks plants can grow on. Some plants will always grow on soul sand.");
		netherGenTerrainIds = config.getStringList("TerrainBlocks", "Generator", new String[] {"minecraft:netherrack", "minecraft:soul_sand", "betternether:nether_mycelium", "betternether:netherrack_moss"}, "Blocks to consider normal terrain during worldgen for structure gen, etc.");
		netherGenReplTerrainIds = config.getStringList("TerrainReplaceBlocks", "Generator", new String[] {"minecraft:netherrack", "minecraft:soul_sand", "minecraft:gravel"}, "Blocks to replace with Better Nether's biome ground covering (if there is one) during worldgen");
		/*for (Field f : BiomeRegister.class.getDeclaredFields()) {
			if (f.getType().isAssignableFrom(NetherBiome.class)) {
				if(!f.getName().toLowerCase(Locale.ROOT).contains("_edge")) {
					if(!f.getName().equals("BIOME_BONE_REEF") && !f.getName().equals("BIOME_POOR_GRASSLANDS")) {
						int weight = config.getInt(f.getName().toLowerCase(Locale.ROOT) + "_weight", "Biomes", 1, 1, Short.MAX_VALUE, "Biome weight (higher = more common)");
						
					} else {
						weights.add(config.getInt(f.getName().toLowerCase(Locale.ROOT) + "_chance", "Biomes", 1, 1, Short.MAX_VALUE, "Chance in 1000 for this sub-biome to appear within the main biome"));
					}
				} else {
					weights.add(-1); // spacer
				}
				items.add(config.getBoolean(f.getName().toLowerCase(Locale.ROOT), "Biomes", true, "Enables|Disables biome"));
			}
		}*/
		enabledBiomes = new HashSet<>();
		everywhereBiomes = new ArrayList<>();
		restrictedBiomes = new HashMap<>();
		for(Map.Entry<String, NetherBiome> e : BiomeRegister.BIOME_REGISTRY.entrySet()) {
			String name = e.getKey();
			NetherBiome biome = e.getValue();
			boolean enabled = config.getBoolean(name, "Biomes", true, "Enables|Disables biome");
			if(enabled) {
				enabledBiomes.add(biome);
			}
			if(!biome.isEdge()) {
				if(biome.isSub()) {
					biome.itemWeight = config.getInt(name + "_chance", "Biomes", biome.getDefaultWeight(), 1, 1000, "Chance in 1000 for this sub-biome to appear within the main biome");
				} else {
					biome.itemWeight = config.getInt(name + "_weight", "Biomes", biome.getDefaultWeight(), 1, Short.MAX_VALUE, "Biome weight (higher = more common)");
					String[] validBiomes = config.getStringList(name + "_spawns", "Biomes", new String[0], "Proper MC-registered biomes this \"biome\" can spawn in. Leave empty for all biomes.");
					if(enabled) {
						if(validBiomes.length == 0) {
							everywhereBiomes.add(biome);
						} else {
							for(String b : validBiomes) {
								restrictedBiomes.computeIfAbsent(b, s -> new ArrayList<>()).add(biome);
							}
						}
					}
				}
			}
		}
		/*registerBiomes = new boolean[items.size()];
		for (int i = 0; i < items.size(); i++)
			registerBiomes[i] = items.get(i);
		biomeWeights = new int[weights.size()];
		weights.toArray(biomeWeights);*/
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
		
		resetItemIndex();
	}
	
	public static void init()
	{
		BNWorldGenerator.setPlantDensity(config.getFloat("GlobalDensity", "Generator", 1, 0, 1, "Global plant density, multiplied on other"));
		BNWorldGenerator.setStructureDensity(config.getFloat("StructureDensity", "Generator", 1F / 16F, 0, 1, "Structure density for random world structures"));
		BNWorldGenerator.setOreDensity(config.getFloat("OreDensity", "Generator", 1F / 1024F, 0, 1, "Cincinnasite ore density"));
		for (NetherBiome biome : enabledBiomes)
		{
			biome.setDensity(config.getFloat(biome.getName().replace(" ", "") + "Density", "Generator", 1, 0, 1, "Density for " + biome.getName() + " biome"));
		}
		for(String s : netherTerrainIds) {
			Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
			if(b == Blocks.AIR) {
				System.out.println("Ignoring non-present block in valid plant blocks: " + s);
			} else {
				NETHER_TERRAIN.add(b);
			}
		}
		for(String s : netherGenTerrainIds) {
			Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
			if(b == Blocks.AIR) {
				System.out.println("Ignoring non-present block in terrain gen terrain blocks: " + s);
			} else {
				NETHER_GEN_TERRAIN.add(b);
			}
		}
		for(String s : netherGenReplTerrainIds) {
			Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
			if(b == Blocks.AIR) {
				System.out.println("Ignoring non-present block in terrain gen terrain replaceable blocks: " + s);
			} else {
				NETHER_GEN_REPL_TERRAIN.add(b);
			}
		}
		System.out.println(NETHER_TERRAIN);
		System.out.println(NETHER_GEN_TERRAIN);
		System.out.println(NETHER_GEN_REPL_TERRAIN);
	}
	
	public static boolean mustInitBiome(NetherBiome biome)
	{
		return enabledBiomes.contains(biome);
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
		enabledBiomes = null;
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
	
	public static List<NetherBiome> getEverywhereBiomes() {
		return everywhereBiomes;
	}
	
	public static Map<String, List<NetherBiome>> getRestrictedBiomes() {
		return restrictedBiomes;
	}
	
	public static boolean isTerrain(Block b) {
		return NETHER_TERRAIN.contains(b);
	}
	
	public static boolean isGenTerrain(Block b) {
		return NETHER_GEN_TERRAIN.contains(b);
	}
	
	public static boolean isReplace(Block b) {
		return NETHER_GEN_REPL_TERRAIN.contains(b);
	}
	
	public static Path getStructureLoadPath() {
		return structureLoadPath;
	}
}
