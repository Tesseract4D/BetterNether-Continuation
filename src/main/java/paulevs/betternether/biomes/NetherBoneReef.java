package paulevs.betternether.biomes;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBone;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BNBlockBone;
import paulevs.betternether.blocks.BlockBoneMushroom;
import paulevs.betternether.blocks.BlocksRegister;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.structures.StructureNBT;

public class NetherBoneReef extends NetherGrasslands
{
	private static StructureNBT[] bones = new StructureNBT[]{
		new StructureNBT("bone_01"),
		new StructureNBT("bone_02"),
		new StructureNBT("bone_03")
	};
	
	public NetherBoneReef(String name)
	{
		super(name);
	}
	
	public boolean isSub() {
		return true;
	}
	public int getDefaultWeight() {
		return

				125;
	}
	
	@Override
	public void genFloorObjects(World chunk, BlockPos pos, Random random)
	{
		Block ground = chunk.getBlockState(pos).getBlock();
		if (random.nextFloat() <= plantDensity)
		{
			if (ConfigLoader.isGenTerrain(ground) || ground == Blocks.SOUL_SAND)
			{
				if (random.nextInt(20) == 0)
					bones[random.nextInt(bones.length)].generateCentered(
							chunk,
							pos.down(random.nextInt(4)),
							random
							);
				else if (BlocksRegister.BLOCK_NETHER_GRASS != Blocks.AIR && random.nextInt(4) != 0 && ConfigLoader.isTerrain(ground))
					chunk.setBlockState(pos.up(), BlocksRegister.BLOCK_NETHER_GRASS.getDefaultState());
			}
			else if (ground instanceof BlockBone || ground instanceof BNBlockBone)
				if (BlocksRegister.BLOCK_BONE_MUSHROOM != Blocks.AIR && random.nextBoolean())
					chunk.setBlockState(pos.up(), BlocksRegister
							.BLOCK_BONE_MUSHROOM
							.getDefaultState()
							.withProperty(BlockBoneMushroom.AGE, random.nextInt(3)));
		}
	}
	
	@Override
	public void genWallObjects(World world, BlockPos origin, BlockPos pos, Random random)
	{
		if (random.nextFloat() <= plantDensity && BlocksRegister.BLOCK_BONE_MUSHROOM != Blocks.AIR && random.nextBoolean() &&
				(world.getBlockState(origin).getBlock() == Blocks.BONE_BLOCK || world.getBlockState(origin).getBlock() == BlocksRegister.BLOCK_BONE))
		{
			BlockPos dir = pos.subtract(origin);
			EnumFacing facing = EnumFacing.getFacingFromVector(dir.getX(), dir.getY(), dir.getZ());
			world.setBlockState(pos, BlocksRegister
					.BLOCK_BONE_MUSHROOM
					.getDefaultState()
					.withProperty(BlockBoneMushroom.FACING, facing)
					.withProperty(BlockBoneMushroom.AGE, random.nextInt(3)));
		}
	}
}
