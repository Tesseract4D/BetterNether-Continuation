package paulevs.betternether.biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBone;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import paulevs.betternether.blocks.BNBlockBone;
import paulevs.betternether.blocks.BlockBoneMushroom;
import paulevs.betternether.blocks.BlocksRegistry;
import paulevs.betternether.config.ConfigLoader;
import paulevs.betternether.structures.StructureNBT;

public class NetherBoneReef extends NetherGrasslands {
	private static final List<StructureNBT> bones = new ArrayList<>();

	static {
		bones.add(new StructureNBT("bone_01"));
		bones.add(new StructureNBT("bone_02"));
		bones.add(new StructureNBT("bone_03"));
	}

	public NetherBoneReef(String name) {
		super(name);
		setFogColor(new Vec3d(0.18, 0.86, 0.79));
	}

	public boolean isSub() {
		return true;
	}

	public int getDefaultWeight() {
		return 125;
	}

	@Override
	public void genFloorObjects(World world, BlockPos pos, Random random) {
		Block ground = world.getBlockState(pos).getBlock();
		if (random.nextFloat() <= plantDensity) {
			if (ConfigLoader.isGenTerrain(ground) || ground == Blocks.SOUL_SAND) {
				if (random.nextInt(20) == 0) {
					StructureNBT structure = bones.get(random.nextInt(bones.size()));
					structure.generateCentered(world, pos.down(random.nextInt(4)), random);
				} else if (BlocksRegistry.BLOCK_NETHER_GRASS != Blocks.AIR && random.nextInt(4) != 0 && ConfigLoader.isTerrain(ground)) {
					world.setBlockState(pos.up(), BlocksRegistry.BLOCK_NETHER_GRASS.getDefaultState());
				}
			} else if (ground instanceof BlockBone || ground instanceof BNBlockBone) {
				if (BlocksRegistry.BLOCK_BONE_MUSHROOM != Blocks.AIR && random.nextBoolean()) {
					int age = random.nextInt(3);
					world.setBlockState(pos.up(), BlocksRegistry.BLOCK_BONE_MUSHROOM.getDefaultState().withProperty(BlockBoneMushroom.AGE, age));
				}
			}
		}
	}

	@Override
	public void genWallObjects(World world, BlockPos origin, BlockPos pos, Random random) {
		if (random.nextFloat() <= plantDensity && BlocksRegistry.BLOCK_BONE_MUSHROOM != Blocks.AIR && random.nextBoolean() &&
				(world.getBlockState(origin).getBlock() == Blocks.BONE_BLOCK || world.getBlockState(origin).getBlock() == BlocksRegistry.BLOCK_BONE)) {
			BlockPos dir = pos.subtract(origin);
			EnumFacing facing = EnumFacing.getFacingFromVector(dir.getX(), dir.getY(), dir.getZ());
			int age = random.nextInt(3);
			world.setBlockState(pos, BlocksRegistry.BLOCK_BONE_MUSHROOM.getDefaultState().withProperty(BlockBoneMushroom.FACING, facing).withProperty(BlockBoneMushroom.AGE, age));
		}
	}
}