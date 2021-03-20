package paulevs.betternether.biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import paulevs.betternether.world.BNWorldGenerator;

public class NetherBiome extends WeightedRandom.Item
{
	static NoiseGeneratorOctaves featureScatter  = new NoiseGeneratorOctaves(new Random(1337), 3);
	float plantDensity = 1;
	String name;
	NetherBiome edge;
	int edgeSize;
	List<NetherBiome> subbiomes;
	int sl;
	
	public NetherBiome(String name)
	{
		super(1);
		this.name = name;
		edge = this;
		edgeSize = 0;
		subbiomes = new ArrayList<NetherBiome>();
	}
	
	public boolean isEdge() {
		return false;
	}
	public boolean isSub() {
		return false;
	}
	public int getDefaultWeight() {
		return 1;
	}
	
	public void genSurfColumn(World world, BlockPos pos, Random random) {}
	
	public void genFloorObjects(World world, BlockPos pos, Random random) {}
	
	public void genWallObjects(World world, BlockPos origin, BlockPos pos, Random random) {}
	
	public void genCeilObjects(World world, BlockPos pos, Random random) {}
	
	protected double getFeatureNoise(BlockPos pos)
	{
		double[] value = new double[1];
		value = featureScatter.generateNoiseOctaves(value, pos.getX(), pos.getZ(), 1, 1, 0.1, 0.1, 1.0);
		return value[0];
	}
	
	public void setDensity(float density)
	{
		plantDensity = density;
	}
	
	public String getName()
	{
		return name;
	}

	public NetherBiome getEdge()
	{
		return edge;
	}
	
	public void setEdge(NetherBiome edge)
	{
		this.edge = edge;
	}

	public int getEdgeSize()
	{
		return edgeSize;
	}
	
	public void setEdgeSize(int size)
	{
		edgeSize = size;
	}
	
	public void addSubBiome(NetherBiome biome)
	{
		subbiomes.add(biome);
	}
	
	public NetherBiome getSubBiome(int x, int y, int z)
	{
		if (subbiomes.size() > 0)
		{
			int id = BNWorldGenerator.getSubBiome(x, y, z);
			NetherBiome b;
			return (b = WeightedRandom.getRandomItem(new Random(id), subbiomes, 1000)) != null ? b : this;
		}
		else
			return this;
	}
	
	public String toString() {
		return name;
	}
}
