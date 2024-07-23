package paulevs.betternether.biomes;

import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import paulevs.betternether.world.BNWorldGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NetherBiome extends WeightedRandom.Item
{
	static NoiseGeneratorOctaves featureScatter  = new NoiseGeneratorOctaves(new Random(1337), 3);
	float plantDensity = 1;
	String name;
	NetherBiome edge;
	Vec3d fogColor = new Vec3d(0.2, 0.03, 0.03);
	int edgeSize;
	List<NetherBiome> subbiomes;
	
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
		return 10;
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

	public Vec3d getFogColor(float f, float partialTicks) {
		return fogColor;
	}

	public void setFogColor(Vec3d f) {
		fogColor = f;
	}
	public void setFogColor(int x, int y, int z) {
		fogColor = new Vec3d(x / 256d, y / 256d, z / 256d);
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
		if (!subbiomes.isEmpty())
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
