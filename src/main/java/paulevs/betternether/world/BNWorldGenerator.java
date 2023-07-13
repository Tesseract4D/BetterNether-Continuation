package paulevs.betternether.world;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherBrick;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import paulevs.betternether.biomes.BiomeRegister;
import paulevs.betternether.biomes.NetherBiome;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.noise.Dither;
import paulevs.betternether.noise.WorleyNoiseIDDistorted3D;
import paulevs.betternether.structures.IStructureWorld;
import paulevs.betternether.structures.StructureAltar;
import paulevs.betternether.structures.StructureBuilding;
import paulevs.betternether.structures.city.CityStructureManager;
import paulevs.betternether.structures.plants.StructureBlackApple;
import paulevs.betternether.structures.plants.StructureEggPlant;
import paulevs.betternether.structures.plants.StructureEye;
import paulevs.betternether.structures.plants.StructureGrayMold;
import paulevs.betternether.structures.plants.StructureInkBush;
import paulevs.betternether.structures.plants.StructureLucis;
import paulevs.betternether.structures.plants.StructureMagmaFlower;
import paulevs.betternether.structures.plants.StructureMedBrownMushroom;
import paulevs.betternether.structures.plants.StructureMedRedMushroom;
import paulevs.betternether.structures.plants.StructureOrangeMushroom;
import paulevs.betternether.structures.plants.StructureRedMold;
import paulevs.betternether.structures.plants.StructureSmoker;
import paulevs.betternether.structures.plants.StructureStalagnate;
import paulevs.betternether.structures.plants.StructureWartCap;
import paulevs.betternether.structures.plants.StructureWartTree;

public class BNWorldGenerator
{
	public static StructureEye eyeGen = new StructureEye();
	public static StructureStalagnate stalagnateGen = new StructureStalagnate();
	public static StructureLucis lucisGen = new StructureLucis();
	public static StructureSmoker smokerGen = new StructureSmoker();
	public static StructureWartTree wartTreeGen = new StructureWartTree();
	public static StructureEggPlant eggPlantGen = new StructureEggPlant();
	public static StructureInkBush inkBushGen = new StructureInkBush();
	public static StructureBlackApple blackAppleGen = new StructureBlackApple();
	public static StructureMagmaFlower magmaFlowerGen = new StructureMagmaFlower();
	public static StructureMedRedMushroom redMushroomGen = new StructureMedRedMushroom();
	public static StructureMedBrownMushroom brownMushroomGen = new StructureMedBrownMushroom();
	public static StructureOrangeMushroom orangeMushroomGen = new StructureOrangeMushroom();
	public static StructureRedMold redMoldGen = new StructureRedMold();
	public static StructureGrayMold grayMoldGen = new StructureGrayMold();
	public static StructureWartCap wartCapGen = new StructureWartCap();

	public static LinkedHashMap<IStructureWorld, Integer> globalStructuresLand = new LinkedHashMap<>();
	public static LinkedHashMap<IStructureWorld, Integer> globalStructuresLava = new LinkedHashMap<>();
	public static LinkedHashMap<IStructureWorld, Integer> globalStructuresCave = new LinkedHashMap<>();

	public static boolean hasCleaningPass = true;
	public static boolean hasEyeGen = true;
	public static boolean hasStalagnateGen = true;
	public static boolean hasLucisGen = true;
	public static boolean hasSmokerGen = true;
	public static boolean hasWartTreeGen = true;
	public static boolean hasEggPlantGen = true;
	public static boolean hasInkBushGen = true;
	public static boolean hasBlackAppleGen = true;
	public static boolean hasMagmaFlowerGen = true;
	public static boolean hasRedMushroomGen = true;
	public static boolean hasBrownMushroomGen = true;
	public static boolean hasOrangeMushroomGen = true;
	public static boolean hasRedMoldGen = true;
	public static boolean hasGrayMoldGen = true;
	public static boolean hasWartsGen = true;

	private static WorleyNoiseIDDistorted3D noise3d;
	private static WorleyNoiseIDDistorted3D subbiomesNoise;
	private static Dither dither;
	private static double biomeSizeXZ;
	private static double biomeSizeY;
	private static double subBiomeSize;
	private static float plantDensity = 1;
	private static float structureDensity = 1F / 64F;
	private static float oreDensity = 1F / 1024F;

	public static boolean enablePlayerDamage;
	public static boolean enableMobDamage;

	private static IBlockState state_air = Blocks.AIR.getDefaultState();

	private static CityStructureManager cityManager;
	private static BlockPos pos;
	private static MutableBlockPos popPos = new MutableBlockPos();

	private static final NetherBiome[][][] BIO_ARRAY = new NetherBiome[8][64][8];

	public static void init(World world)
	{
		long seed = world.getSeed();
		noise3d = new WorleyNoiseIDDistorted3D(seed, Integer.MAX_VALUE);
		subbiomesNoise = new WorleyNoiseIDDistorted3D(~seed, 256);
		dither = new Dither(seed);
		if (ConfigLoader.hasCities())
		{
			cityManager = new CityStructureManager(seed);
			cityManager.load(world);
			cityManager.setDistance(ConfigLoader.getCityDistance());
		}
	}

	public static void save(World world)
	{
		if (cityManager != null)
			cityManager.save(world);
	}

	private static void makeBiomeArray(World world, int sx, int sz) {
		final NetherBiome[] id = new NetherBiome[1];
		final int[] wx = new int[1];
		final int[] wy = new int[1];
		final int[] wz = new int[1];
		Map<BlockPos, NetherBiome> biomeMap = new ConcurrentHashMap<>();

		for (int x = 0; x < 8; x++) {
			wx[0] = sx | (x << 1);
			for (int z = 0; z < 8; z++) {
				wz[0] = sz | (z << 1);
				NetherBiome edge = null;
				for (int y = 0; y < 64; y++) {
					wy[0] = (y << 1);
					BlockPos pos = new BlockPos(wx[0], wy[0], wz[0]);
					if (!biomeMap.containsKey(pos)) {
						id[0] = getBiome(world, wx[0], wy[0], wz[0]);
						biomeMap.put(pos, id[0]);
						if (isEdge(world, id[0], wx[0], wy[0], wz[0], id[0].getEdgeSize()))
							edge = id[0].getEdge();
						else
							edge = id[0].getSubBiome(wx[0], wy[0], wz[0]);
					} else {
						id[0] = biomeMap.get(pos);
						if (edge != null && isEdge(world, id[0], wx[0], wy[0], wz[0], edge.getEdgeSize()))
							id[0] = edge.getEdge();
						else if (edge != null)
							id[0] = edge.getSubBiome(wx[0], wy[0], wz[0]);
						biomeMap.put(pos, id[0]);
					}
					BIO_ARRAY[x][y][z] = id[0];
				}
			}
		}
	}

	private static final NetherBiome[][][] biomeCache = new NetherBiome[16][128][16];

	private static NetherBiome getBiomeLocal(int x, int y, int z, World world, Random random) {
		x = (x + random.nextInt(2)) >> 1;
		if (x > 7)
			x = 7;
		y = (y + random.nextInt(2)) >> 1;
		if (y > 63)
			y = 63;
		z = (z + random.nextInt(2)) >> 1;
		if (z > 7)
			z = 7;

		// Verify if the biome already exists in the cache
		NetherBiome biome = biomeCache[x][y][z];
		if (biome != null) {
			return biome;
		}

		// Otherwise, generate a new biome and store it in the cache.
		biome = BIO_ARRAY[x][y][z];
		if (isEdge(world, biome, x, y, z, biome.getEdgeSize()))
			biome = biome.getEdge();
		else
			biome = biome.getSubBiome(x, y, z);
		biomeCache[x][y][z] = biome;
		return biome;
	}

	public static void generate(World world, int cx, int cz, Random r) {
		if (world.isRemote) {
			return;
		}

		AtomicReference<NetherBiome> biome = new AtomicReference<>(null);
		int sx = (cx << 4) | 8;
		int sz = (cz << 4) | 8;

		// Structure Generator
		if (r.nextFloat() < structureDensity) {
			BlockPos pos = new BlockPos(sx + r.nextInt(8), 32 + r.nextInt(120 - 32), sz + r.nextInt(8));
			IBlockState blockState;
			while ((blockState = world.getBlockState(pos)).getBlock() != Blocks.AIR && pos.getY() > 32) {
				pos = pos.down();
			}
			pos = downRay(world, pos);
			if (pos != null) {
				boolean terrain = true;
				for (int y = 1; y < 8; y++) {
					if (world.getBlockState(pos.up(y)).getBlock() != Blocks.AIR) {
						terrain = false;
						break;
					}
				}
				if (terrain) {
					if (globalStructuresLava.size() > 0 && ConfigLoader.getTotalWeightLava() > 0 && world.getBlockState(pos).getMaterial() == Material.LAVA)
						calculateWeightedMap(globalStructuresLava, ConfigLoader.getTotalWeightLava(), r).generateLava(world, pos.up(), r);
					else if (globalStructuresLand.size() > 0 && ConfigLoader.getTotalWeightLand() > 0)
						calculateWeightedMap(globalStructuresLand, ConfigLoader.getTotalWeightLand(), r).generateSurface(world, pos.up(), r);
				} else if (globalStructuresCave.size() > 0 && ConfigLoader.getTotalWeightCave() > 0) {
					calculateWeightedMap(globalStructuresCave, ConfigLoader.getTotalWeightCave(), r).generateSubterrain(world, pos, r);
				}
			}
		}

		makeBiomeArray(world, sx, sz);
		NetherBiome[][] biomeCache = new NetherBiome[16][16]; // Cache for biome lookup results

		// Total Populator
		List<BlockPos> blocksToPopulate = new ArrayList<>();
		for (int x = 0; x < 16; x++) {
			int wx = sx + x;
			for (int z = 0; z < 16; z++) {
				int wz = sz + z;
				for (int y = 5; y < 126; y++) {
					BlockPos popPos = new BlockPos(wx, y, wz);
					IBlockState blockState = world.getBlockState(popPos);
					if (blockState.isFullBlock()) {
						blocksToPopulate.add(popPos.toImmutable());
					}
				}
			}
		}

		for (BlockPos pos : blocksToPopulate) {
			IBlockState blockState = world.getBlockState(pos);
			if (blockState.isFullBlock()) {
				biome.set(getBiomeFromCache(pos.getX() & 15, pos.getZ() & 15, biomeCache, world, r));

				// Ground Generation
				if (world.getBlockState(pos.up()).getBlock() == Blocks.AIR) {
					biome.get().genSurfColumn(world, pos, r);
					if (r.nextFloat() <= plantDensity)
						biome.get().genFloorObjects(world, pos, r);
				}

				// Ceiling Generation
				if (world.getBlockState(pos.down()).getBlock() == Blocks.AIR) {
					if (r.nextFloat() <= plantDensity)
						biome.get().genCeilObjects(world, pos, r);
				}

				// Wall Generation
				else if (((pos.getX() + pos.getY() + pos.getZ()) & 1) == 0) {
					boolean bNorth = world.getBlockState(pos.north()).getBlock() == Blocks.AIR;
					boolean bSouth = world.getBlockState(pos.south()).getBlock() == Blocks.AIR;
					boolean bEast = world.getBlockState(pos.east()).getBlock() == Blocks.AIR;
					boolean bWest = world.getBlockState(pos.west()).getBlock() == Blocks.AIR;
					if (bNorth || bSouth || bEast || bWest) {
						BlockPos objPos = null;
						if (bNorth)
							objPos = pos.north();
						else if (bSouth)
							objPos = pos.south();
						else if (bEast)
							objPos = pos.east();
						else
							objPos = pos.west();
						boolean bDown = world.getBlockState(objPos.up()).getBlock() == Blocks.AIR;
						boolean bUp = world.getBlockState(objPos.down()).getBlock() == Blocks.AIR;
						if (bDown && bUp) {
							if (r.nextFloat() <= plantDensity)
								biome.get().genWallObjects(world, pos, objPos, r);
							if (pos.getY() < 37 && world.getBlockState(pos).getBlock() instanceof BlockNetherBrick && r.nextInt(512) == 0)
								wartCapGen.generate(world, pos, r);
						}
					}
				}

				if (BlocksRegister.BLOCK_CINCINNASITE_ORE != Blocks.AIR && r.nextFloat() < oreDensity)
					spawnOre(BlocksRegister.BLOCK_CINCINNASITE_ORE.getDefaultState(), world, pos, r);
			}
		}
	}

	private static NetherBiome getBiomeFromCache(int x, int z, NetherBiome[][] biomeCache, World world, Random r) {
		if (biomeCache[x][z] == null) {
			biomeCache[x][z] = getBiomeLocal(x, 0, z, world, r);
		}
		return biomeCache[x][z];
	}


	private static boolean isEdge(World world, NetherBiome centerID, int x, int y, int z, int distance) {
		if (distance <= 0) {
			return false;
		}
		Map<String, NetherBiome> biomeCache = new HashMap<>(); // create a new cache
		for (int i = -distance; i <= distance; i += distance * 2) {
			for (int j = -distance; j <= distance; j += distance * 2) {
				for (int k = -distance; k <= distance; k += distance * 2) {
					String key = String.format("%d_%d_%d", x + i, y + j, z + k);
					NetherBiome biome = biomeCache.get(key);
					if (biome == null) {
						biome = getBiome(world, x + i, y + j, z + k);
						biomeCache.put(key, biome);
					}
					if (centerID != biome) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static NetherBiome getBiome(World world, int x, int y, int z)
	{
		double px = (double) dither.ditherX(x, y, z) * biomeSizeXZ;
		double py = (double) dither.ditherY(x, y, z) * biomeSizeY;
		double pz = (double) dither.ditherZ(x, y, z) * biomeSizeXZ;
		Biome biome = world.getBiome(new BlockPos(x, y, z));
		return WeightedRandom.getRandomItem(new Random(noise3d.GetValue(px, py, pz)), BiomeRegister.getBiomesForMCBiome(biome));
	}

	private static boolean isAir(World chunk, BlockPos pos)
	{
		return chunk.getBlockState(pos).getBlock() == Blocks.AIR;
	}

	private static boolean canReplace(World chunk, BlockPos pos)
	{
		return !isAir(chunk, pos) && ConfigLoader.isReplace(chunk.getBlockState(pos).getBlock());
	}

	private static void spawnOre(IBlockState state, World world, BlockPos pos, Random random)
	{
		for (int i = 0; i < 6 + random.nextInt(11); i++)
		{
			BlockPos local = pos.add(random.nextInt(3), random.nextInt(3), random.nextInt(3));
			if (world.getBlockState(local).getBlock() == Blocks.NETHERRACK)
			{
				world.setBlockState(local, state);
			}
		}
	}

	public static void updateGenSettings()
	{
		biomeSizeXZ = 1.0 / (double) ConfigLoader.getBiomeSizeXZ();
		biomeSizeY = 1.0 / (double) ConfigLoader.getBiomeSizeY();
		subBiomeSize = biomeSizeXZ * 3;
		hasCleaningPass = ConfigLoader.hasCleaningPass();
		hasEyeGen = BlocksRegister.BLOCK_EYE_VINE != Blocks.AIR &&
				BlocksRegister.BLOCK_EYEBALL != Blocks.AIR &&
				BlocksRegister.BLOCK_EYEBALL_SMALL != Blocks.AIR;
		hasStalagnateGen = BlocksRegister.BLOCK_STALAGNATE_BOTTOM != Blocks.AIR &&
				BlocksRegister.BLOCK_STALAGNATE_MIDDLE != Blocks.AIR &&
				BlocksRegister.BLOCK_STALAGNATE_TOP != Blocks.AIR;
		hasLucisGen = BlocksRegister.BLOCK_LUCIS_MUSHROOM != Blocks.AIR;
		hasSmokerGen = BlocksRegister.BLOCK_SMOKER != Blocks.AIR;
		hasEggPlantGen = BlocksRegister.BLOCK_EGG_PLANT != Blocks.AIR;
		hasInkBushGen = BlocksRegister.BLOCK_INK_BUSH != Blocks.AIR;
		hasBlackAppleGen = BlocksRegister.BLOCK_BLACK_APPLE != Blocks.AIR;
		hasMagmaFlowerGen = BlocksRegister.BLOCK_MAGMA_FLOWER != Blocks.AIR;
		hasRedMushroomGen = BlocksRegister.BLOCK_RED_LARGE_MUSHROOM != Blocks.AIR;
		hasBrownMushroomGen = BlocksRegister.BLOCK_BROWN_LARGE_MUSHROOM != Blocks.AIR;
		hasOrangeMushroomGen = BlocksRegister.BLOCK_ORANGE_MUSHROOM != Blocks.AIR;
		hasRedMoldGen = BlocksRegister.BLOCK_RED_MOLD != Blocks.AIR;
		hasGrayMoldGen = BlocksRegister.BLOCK_GRAY_MOLD != Blocks.AIR;
		hasWartsGen = ConfigLoader.hasNetherWart();

		globalStructuresLand.clear();
		for (int i = 0; i < ConfigLoader.getScInfosLand().length; i++) {
			ConfigLoader.StructureConfigInfo info = ConfigLoader.getScInfosLand()[i];
			if ("altar".equals(info.name)) {
				globalStructuresLand.put(new StructureAltar(), info.weight);
			} else {
				globalStructuresLand.put(new StructureBuilding(info.name, info.offsetY), info.weight);
			}
		}

		globalStructuresLava.clear();
		for (int i = 0; i < ConfigLoader.getScInfosLava().length; i++) {
			ConfigLoader.StructureConfigInfo info = ConfigLoader.getScInfosLava()[i];
			if ("altar".equals(info.name)) {
				globalStructuresLava.put(new StructureAltar(), info.weight);
			} else {
				globalStructuresLava.put(new StructureBuilding(info.name, info.offsetY), info.weight);
			}
		}

		globalStructuresCave.clear();
		for (int i = 0; i < ConfigLoader.getScInfosCave().length; i++) {
			ConfigLoader.StructureConfigInfo info = ConfigLoader.getScInfosCave()[i];
			if ("altar".equals(info.name)) {
				globalStructuresCave.put(new StructureAltar(), info.weight);
			} else {
				globalStructuresCave.put(new StructureBuilding(info.name, info.offsetY), info.weight);
			}
		}
	}

	private static <T> T calculateWeightedMap(Map<T, Integer> map, int totalWeight, Random random) {
		double r = random.nextDouble() * totalWeight;
		double countWeight = 0.0;
		for (Map.Entry<T, Integer> item : map.entrySet()) {
			countWeight += item.getValue();
			if (countWeight >= r)
				return item.getKey();
		}
		return null;
	}

	private static BlockPos downRay(World world, BlockPos start)
	{
		Block b;
		BlockPos p;
		for (int j = start.getY(); j > 31; j--)
		{
			p = new BlockPos(start.getX(), j, start.getZ());
			b = world.getBlockState(p).getBlock();
			if (b != Blocks.AIR && (ConfigLoader.isGenTerrain(b) || world.getBlockState(p).getMaterial() == Material.LAVA))
			{
				return new BlockPos(start.getX(), j, start.getZ());
			}
		}
		return null;
	}

	public static void setPlantDensity(float density)
	{
		plantDensity = density;
	}

	public static void setStructureDensity(float density)
	{
		structureDensity = density;
	}

	public static int getSubBiome(int x, int y, int z)
	{
		double px = (double) dither.ditherX(x, y, z) * subBiomeSize;
		double py = (double) dither.ditherY(x, y, z) * subBiomeSize;
		double pz = (double) dither.ditherZ(x, y, z) * subBiomeSize;
		return subbiomesNoise.GetValue(px, py, pz);
	}

	public static NetherBiome getBiome(World world, BlockPos pos)
	{
		NetherBiome biome = getBiome(world, pos.getX(), pos.getY(), pos.getZ());
		if (isEdge(world, biome, pos.getX(), pos.getY(), pos.getZ(), biome.getEdgeSize()))
			biome = biome.getEdge();
		else
			biome = biome.getSubBiome(pos.getX(), pos.getY(), pos.getZ());
		return biome;
	}

	public static BlockPos getNearestCity(World world, int cx, int cz)
	{
		return cityManager.getNearestStructure(world, cx, cz);
	}

	public static void setOreDensity(float density)
	{
		oreDensity = density;
	}
}
