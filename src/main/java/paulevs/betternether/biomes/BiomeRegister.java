package paulevs.betternether.biomes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import paulevs.betternether.config.ConfigLoader;

public class BiomeRegister
{
	public static int biomeCount;
	public static NetherBiome[] usedBiomes;
	public static List<NetherBiome> usedBiomesList;
	public static NetherBiome defaultBiome;
	public static final Map<String, NetherBiome> BIOME_REGISTRY = new HashMap<>();
	public static final NetherBiome BIOME_EMPTY_NETHER = register("empty_nether", new NetherBiome("Empty Nether"));
	public static final NetherBiome BIOME_GRAVEL_DESERT = register("gravel_desert", new NetherBiomeGravelDesert("Gravel Desert"));
	public static final NetherBiome BIOME_NETHER_JUNGLE = register("nether_jungle", new NetherBiomeJungle("Nether Jungle"));
	public static final NetherBiome BIOME_WART_FOREST = register("wart_forest", new NetherWartForest("Wart Forest"));
	public static final NetherBiome BIOME_GRASSLANDS = register("grasslands", new NetherGrasslands("Nether Grasslands"));
	public static final NetherBiome BIOME_MUSHROOM_FOREST = register("mushroom_forest", new NetherMushroomForest("Nether Mushroom Forest"));
	public static final NetherBiome BIOME_MUSHROOM_FOREST_EDGE = register("mushroom_forest_edge", new NetherMushroomForestEdge("Nether Mushroom Forest Edge"));
	public static final NetherBiome BIOME_WART_FOREST_EDGE = register("wart_forest_edge", new NetherWartForestEdge("Nether Wart Forest Edge"));
	public static final NetherBiome BIOME_BONE_REEF = register("bone_reef", new NetherBoneReef("Bone Reef"));
	public static final NetherBiome BIOME_POOR_GRASSLANDS = register("poor_grasslands", new NetherPoorGrasslands("Poor Nether Grasslands"));
	
	public static void registerBiomes()
	{
		List<NetherBiome> biomes = new ArrayList<NetherBiome>();
		useBiome(BIOME_EMPTY_NETHER, biomes);
		useBiome(BIOME_GRAVEL_DESERT, biomes);
		useBiome(BIOME_NETHER_JUNGLE, biomes);
		useBiome(BIOME_WART_FOREST, biomes);
		useBiome(BIOME_GRASSLANDS, biomes);
		useBiome(BIOME_MUSHROOM_FOREST, biomes);
		useEdgeBiome(BIOME_MUSHROOM_FOREST_EDGE, BIOME_MUSHROOM_FOREST, 10);
		useEdgeBiome(BIOME_WART_FOREST_EDGE, BIOME_WART_FOREST, 9);
		useSubBiome(BIOME_BONE_REEF, BIOME_GRASSLANDS);
		useSubBiome(BIOME_POOR_GRASSLANDS, BIOME_GRASSLANDS);
		biomeCount = biomes.size();
		usedBiomesList = biomes;
		usedBiomes = new NetherBiome[biomeCount];
		for (int i = 0; i < biomeCount; i++)
			usedBiomes[i] = biomes.get(i);
	}
	
	private static NetherBiome register(String id, NetherBiome biome) {
		BIOME_REGISTRY.put(id, biome);
		return biome;
	}
	
	private static void useBiome(NetherBiome biome, List<NetherBiome> biomes)
	{
		if (ConfigLoader.mustInitBiome(biome))
		{
			biomes.add(biome);
		}
	}
	
	private static void useEdgeBiome(NetherBiome biome, NetherBiome mainBiome, int size)
	{
		if (ConfigLoader.mustInitBiome(biome))
		{
			mainBiome.setEdge(biome);
			mainBiome.setEdgeSize(size);
		}
	}
	
	private static void useSubBiome(NetherBiome biome, NetherBiome mainBiome)
	{
		if (ConfigLoader.mustInitBiome(biome))
		{
			mainBiome.addSubBiome(biome);
		}
	}
	
	public static NetherBiome getBiomeID(int id)
	{
		return usedBiomes[id];
	}
	
	public static NetherBiome[] getBiomes()
	{
		return usedBiomes;
	}
}
