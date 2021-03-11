package paulevs.betternether.structures.city;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.noise.OpenSimplexNoise;
import paulevs.betternether.noise.WorleyNoise;
import paulevs.betternether.structures.big.BigStructure;
import paulevs.betternether.structures.big.StructureManager;

public class CityStructureManager extends StructureManager
{
	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState LAVA = Blocks.LAVA.getDefaultState();
	
	protected CityGenerator generator = new CityGenerator();
	private final OpenSimplexNoise noise;
	private final WorleyNoise noiseX;
	
	public CityStructureManager(long seed)
	{
		super("city", 80, seed);
		random.setSeed(seed);
		noise = new OpenSimplexNoise(random.nextLong());
		noiseX = new WorleyNoise(random.nextLong());
	}

	@Override
	protected BigStructure makeStructure(int cx, int cz)
	{
		setSeed(cx, cz);
		BlockPos pos = new BlockPos(cx << 4, 40, cz << 4);
		BigStructureCity cave = new BigStructureCity(pos, cx, cz, generator, random);
		int caveSize = (int) (cave.getCitySide() * 0.6) + random.nextInt(50);
		makeCave(caveSize, 40, cave);
		List<BlockPos> positions = cave.getPosList(pos);
		List<Integer> radiuses = cave.getRadiuses();
		for (int i = 0; i < positions.size(); i++)
		{
			BlockPos cp = positions.get(i);
			int r = radiuses.get(i);
			makeCave(r, cp.getX(), cp.getY(), cp.getZ(), cave);
		}
		return cave;
	}
	
	protected void makeCave(int radius, int centerY, BigStructure structure)
	{
		int bounds = (int) (radius * 1.5);
		int rr = radius * radius;
		int minY = 5 - centerY;
		int lavaH = 31 - centerY;
		for (int x = -bounds; x < bounds; x++)
		{
			for (int y = minY; y < radius; y++)
			{
				int wy = y + centerY - 40;
				int y2 = y * 2;
				for (int z = -bounds; z < bounds; z++)
				{
					/*double dx = x + noiseX.GetValue(y2 * 0.02, z * 0.02) * 20;
					double dy = y2 + noiseX.GetValue(x * 0.02, z * 0.02) * 20;
					double dz = z + noiseX.GetValue(y * 0.02, x * 0.02) * 20;*/
					/*double dx = noise.eval(y2 * 0.02, z * 0.02) * radius;
					double dy = noise.eval(x * 0.02, z * 0.02) * radius;
					double dz = noise.eval(y * 0.02, x * 0.02) * radius;*/
 					double xx = x * x;
					double yy = y2 * y2;
					double zz = z * z;
					double posRadius = radius - (Math.abs((noise.eval(x * 0.075, y * 0.075, z * 0.075) * 20)) + 10);
					if (xx + yy + zz < posRadius * posRadius) 
					{
						if (wy > lavaH)
							structure.setBlock(AIR, new BlockPos(x, wy, z));
						else
							structure.setBlock(LAVA, new BlockPos(x, wy, z));
					}
				}
			}
		}
	}
	
	protected void makeCave(int radius, int centerX, int centerY, int centerZ, BigStructure structure)
	{
		int bounds = (int) (radius * 1.5);
		int rr = radius * radius;
		int minY = 5 - centerY;
		int lavaH = 31 - centerY;
		for (int x = -bounds; x < bounds; x++)
		{
			int wx = x + centerX;
			for (int y = minY; y < bounds; y++)
			{
				int wy = y + centerY - 40;
				for (int z = -bounds; z < bounds; z++)
				{
					int wz = z + centerZ;
					/*double dx = noise.eval(y * 0.02, z * 0.02) * radius - 10;
					double dy = noise.eval(x * 0.02, z * 0.02) * radius - 10;
					double dz = noise.eval(y * 0.02, x * 0.02) * radius - 10;*/
					double y2 = y * 3;
					double xx = x * x;
					double yy = y2 * y2;
					double zz = z * z;
					double posRadius = radius - (Math.abs((noise.eval(x * 0.075, y * 0.075, z * 0.075) * 20)) + 10);
					if (xx + yy + zz < posRadius * posRadius) 
					{
						if (wy > lavaH)
							structure.setBlock(AIR, new BlockPos(wx, wy, wz));
						else
							structure.setBlock(LAVA, new BlockPos(wx, wy, wz));
					}
				}
			}
		}
	}
	
	private static int fastAbs(int n) {
		int mask = n >> (32 - 1);
		return ((n + mask) ^ mask);
	}

	
	@Override
	public void load(World world)
	{
		String path = world.getSaveHandler().getWorldDirectory().getAbsolutePath() + "/data/bn_" + name + ".nbt";
		File file = new File(path);
		if (file.exists())
		{
			try
			{
				FileInputStream fs = new FileInputStream(file);
				NBTTagCompound root = CompressedStreamTools.readCompressed(fs);
				fs.close();
				NBTTagList structureData = root.getTagList("structures", 10);
				for (int i = 0; i < structureData.tagCount(); i++)
				{
					BigStructureCity city = new BigStructureCity(structureData.getCompoundTagAt(i), generator);
					structures.add(city);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
