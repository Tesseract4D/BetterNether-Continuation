package paulevs.betternether.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherBrick;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
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
import paulevs.betternether.structures.plants.*;

import java.util.*;

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

	private static void makeBiomeArray(World world, int sx, int sz)
	{
		NetherBiome id;
		int wx, wy, wz;
		for (int x = 0; x < 8; x++)
		{
			wx = sx | (x << 1);
			for (int y = 0; y < 64; y++)
			{
				wy = (y << 1);
				for (int z = 0; z < 8; z++)
				{
					wz = sz | (z << 1);
					id = getBiome(world, wx, wy, wz);
					BIO_ARRAY[x][y][z] = id;
					if (isEdge(world, id, wx, wy, wz, BIO_ARRAY[x][y][z].getEdgeSize()))
						BIO_ARRAY[x][y][z] = BIO_ARRAY[x][y][z].getEdge();
					else
						BIO_ARRAY[x][y][z] = BIO_ARRAY[x][y][z].getSubBiome(wx, wy, wz);
				}
			}
		}
	}

	private static NetherBiome getBiomeLocal(int x, int y, int z, Random random)
	{
		x = (x + random.nextInt(2)) >> 1;
		if (x > 7)
			x = 7;
		y = (y + random.nextInt(2)) >> 1;
		if (y > 63)
			y = 63;
		z = (z + random.nextInt(2)) >> 1;
		if (z > 7)
			z = 7;
		return BIO_ARRAY[x][y][z];
	}

	public static void generate(World world, int cx, int cz, Random r)
	{
		if (!world.isRemote)
		{
			Random random = new Random(world.getSeed() ^ (new ChunkPos(cx, cz).hashCode() * 49376522L));
			NetherBiome biome;
			int sx = (cx << 4) | 8;
			int sz = (cz << 4) | 8;

			// Structure Generator
			if (random.nextFloat() < structureDensity)
			{
				pos = new BlockPos(sx + random.nextInt(8), 32 + random.nextInt(120 - 32), sz + random.nextInt(8));
				while (world.getBlockState(pos).getBlock() != Blocks.AIR && pos.getY() > 32)
				{
					pos = pos.down();
				}
				pos = downRay(world, pos);
				if (pos != null)
				{
					boolean terrain = true;
					for (int y = 1; y < 8; y++)
					{
						if (world.getBlockState(pos.up(y)).getBlock() != Blocks.AIR)
						{
							terrain = false;
							break;
						}
					}
					if (terrain)
					{
						if (globalStructuresLava.size() > 0 && ConfigLoader.getTotalWeightLava() > 0 && world.getBlockState(pos).getMaterial() == Material.LAVA)
							calculateWeightedMap(globalStructuresLava, ConfigLoader.getTotalWeightLava(), random).generateLava(world, pos.up(), random);
						else if (globalStructuresLand.size() > 0 && ConfigLoader.getTotalWeightLand() > 0)
							calculateWeightedMap(globalStructuresLand, ConfigLoader.getTotalWeightLand(), random).generateSurface(world, pos.up(), random);
					}
					else if (globalStructuresCave.size() > 0 && ConfigLoader.getTotalWeightCave() > 0)
					{
						calculateWeightedMap(globalStructuresCave, ConfigLoader.getTotalWeightCave(), random).generateSubterrain(world, pos, random);
					}
				}
			}

			makeBiomeArray(world, sx, sz);

			// Total Populator
			for (int x = 0; x < 16; x++)
			{
				int wx = sx + x;
				for (int z = 0; z < 16; z++)
				{
					int wz = sz + z;
					for (int y = 5; y < 126; y++)
					{
						//pos = new BlockPos(wx, y, wz);
						popPos.setPos(wx, y, wz);
						if (world.getBlockState(popPos).isFullBlock())
						{

							biome = getBiomeLocal(x, y, z, random);

							// Ground Generation
							if (world.getBlockState(popPos.up()).getBlock() == Blocks.AIR)
							{
								biome.genSurfColumn(world, popPos, random);
								if (random.nextFloat() <= plantDensity)
									biome.genFloorObjects(world, popPos, random);
							}

							// Ceiling Generation
							else if (world.getBlockState(popPos.down()).getBlock() == Blocks.AIR)
							{
								if (random.nextFloat() <= plantDensity)
									biome.genCeilObjects(world, popPos, random);
							}

							// Wall Generation
							else if (((x + y + z) & 1) == 0)
							{
								boolean bNorth = world.getBlockState(popPos.north()).getBlock() == Blocks.AIR;
								boolean bSouth = world.getBlockState(popPos.south()).getBlock() == Blocks.AIR;
								boolean bEast = world.getBlockState(popPos.east()).getBlock() == Blocks.AIR;
								boolean bWest = world.getBlockState(popPos.west()).getBlock() == Blocks.AIR;
								if (bNorth || bSouth || bEast || bWest)
								{
									BlockPos objPos = null;
									if (bNorth)
										objPos = popPos.north();
									else if (bSouth)
										objPos = popPos.south();
									else if (bEast)
										objPos = popPos.east();
									else
										objPos = popPos.west();
									boolean bDown = world.getBlockState(objPos.up()).getBlock() == Blocks.AIR;
									boolean bUp = world.getBlockState(objPos.down()).getBlock() == Blocks.AIR;
									if (bDown && bUp)
									{
										if (random.nextFloat() <= plantDensity)
											biome.genWallObjects(world, popPos, objPos, random);
										if (y < 37 && world.getBlockState(popPos).getBlock() instanceof BlockNetherBrick && random.nextInt(512) == 0)
											wartCapGen.generate(world, popPos, random);
									}
								}
							}
						}
						if (BlocksRegister.BLOCK_CINCINNASITE_ORE != Blocks.AIR && random.nextFloat() < oreDensity)
							spawnOre(BlocksRegister.BLOCK_CINCINNASITE_ORE.getDefaultState(), world, popPos, random);
					}
				}
			}
		}
	}

	private static boolean isEdge(World world, NetherBiome centerID, int x, int y, int z, int distance)
	{
		return distance > 0 && (centerID != getBiome(world, x + distance, y, z) ||
				centerID != getBiome(world, x - distance, y, z) ||
				centerID != getBiome(world, x, y + distance, z) ||
				centerID != getBiome(world, x, y - distance, z) ||
				centerID != getBiome(world, x, y, z + distance) ||
				centerID != getBiome(world, x, y, z - distance));
	}

	public static NetherBiome getBiome(World world, int x, int y, int z)
	{
		double px = (double) dither.ditherX(x, y, z) * biomeSizeXZ;
		double py = (double) dither.ditherY(x, y, z) * biomeSizeY;
		double pz = (double) dither.ditherZ(x, y, z) * biomeSizeXZ;
		Biome biome = world.getBiome(new BlockPos(x, y, z));
		List<NetherBiome> l = BiomeRegister.getBiomesForMCBiome(biome);
		return l.isEmpty() ? BiomeRegister.BIOME_EMPTY_NETHER : WeightedRandom.getRandomItem(new Random(noise3d.GetValue(px, py, pz)), l);
	}

	// generate cities
	public static void smoothChunk(World world, int cx, int cz)
	{
		if (hasCleaningPass)
		{
			int wx = (cx << 4) | 8;
			int wz = (cz << 4) | 8;
			HashSet<BlockPos> posToReplace = new HashSet<>();
			BlockPos up;
			BlockPos down;
			BlockPos north;
			BlockPos south;
			BlockPos east;
			BlockPos west;

			for (int y = 32; y < 110; y++)
			{
				for (int x = 0; x < 16; x++)
				{
					for (int z = 0; z < 16; z++)
					{
						popPos.setPos(x + wx, y, z + wz);
						if (canReplace(world, popPos))
						{
							up = popPos.up();
							down = popPos.down();
							north = popPos.north();
							south = popPos.south();
							east = popPos.east();
							west = popPos.west();

							if (isAir(world, north) && isAir(world, south))
							{
								posToReplace.add(new BlockPos(popPos));
							}
							else if (isAir(world, east) && isAir(world, west))
							{
								posToReplace.add(new BlockPos(popPos));
							}
							else if (isAir(world, up) && isAir(world, down))
							{
								posToReplace.add(new BlockPos(popPos));
							}
							else if (isAir(world, popPos.north().east().down()) && isAir(world, popPos.south().west().up()))
							{
								posToReplace.add(new BlockPos(popPos));
							}
							else if (isAir(world, popPos.south().east().down()) && isAir(world, popPos.north().west().up()))
							{
								posToReplace.add(new BlockPos(popPos));
							}
							else if (isAir(world, popPos.north().west().down()) && isAir(world, popPos.south().east().up()))
							{
								posToReplace.add(new BlockPos(popPos));
							}
							else if (isAir(world, popPos.south().west().down()) && isAir(world, popPos.north().east().up()))
							{
								posToReplace.add(new BlockPos(popPos));
							}
						}
					}
				}
			}

			// Process 16 blocks per tick
			int blocksPerTick = 16;
			int processedBlocks = 0;

			for (BlockPos p : posToReplace)
			{
				world.setBlockState(p, state_air);

				processedBlocks++;
				if (processedBlocks >= blocksPerTick)
				{
					// Save the progress and return for the next tick
					return;
				}
			}
		}

		if (cityManager != null)
			cityManager.generate(world, cx, cz);
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
