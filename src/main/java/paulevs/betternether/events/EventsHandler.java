package paulevs.betternether.events;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherrack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import paulevs.betternether.blocks.BlockBrownLargeMushroom;
import paulevs.betternether.blocks.BlockRedLargeMushroom;
import paulevs.betternether.blocks.BlocksRegistry;
import paulevs.betternether.commands.CommandsRegister;
import paulevs.betternether.fix.FixesCommon;
import paulevs.betternether.world.BNWorldGenerator;

import java.util.Random;

public class EventsHandler
{
	static NoiseGeneratorOctaves featureScatter;
	static MinecraftServer server;

	public static void init()
	{
		featureScatter = new NoiseGeneratorOctaves(new Random(1337), 3);
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		if (event.getWorld().provider.getDimensionType() == DimensionType.NETHER && !event.getWorld().isRemote)
		{
			BNWorldGenerator.init(event.getWorld());
			server = event.getWorld().getMinecraftServer();
			CommandsRegister.register((ServerCommandManager) server.getCommandManager());
		}
	}
	
	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save event)
	{
		if (event.getWorld().provider.getDimensionType() == DimensionType.NETHER && !event.getWorld().isRemote)
		{
			BNWorldGenerator.save(event.getWorld());
		}
	}

	@SubscribeEvent
	public void onPopulate(PopulateChunkEvent.Post event)
	{
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimensionType() == DimensionType.NETHER)
		{
			World world = event.getWorld();
			BNWorldGenerator.generate(world, event.getChunkX(), event.getChunkZ(), world.rand);
		}
	}
	@SubscribeEvent (priority = EventPriority.HIGHEST)
	public void onPrePopulate(PopulateChunkEvent.Pre event)
	{
		if (!event.getWorld().isRemote && event.getWorld().provider.getDimensionType() == DimensionType.NETHER)
		{
			BNWorldGenerator.smoothChunk(event.getWorld(), event.getChunkX(), event.getChunkZ());
		}
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent e) {
		if (FixesCommon.fogFadeTime > 0)
			FixesCommon.fogFadeTime--;
		else
			FixesCommon.fadingFog = null;
	}

	@SubscribeEvent
	public void boneGrow(BonemealEvent event)
	{
		Block block = event.getBlock().getBlock();
		World world = event.getWorld();
		Random random = world.rand;
		if (!world.isRemote && block instanceof BlockNetherrack)
		{
			IBlockState grass = BlocksRegistry.BLOCK_NETHER_GRASS.getDefaultState();
			IBlockState moss = BlocksRegistry.BLOCK_NETHERRACK_MOSS.getDefaultState();
			BlockPos bpos = event.getPos();
			if (world.getBlockState(bpos.up()).getBlock() == Blocks.AIR)
			{
				world.setBlockState(bpos.up(), grass);
				for (int i = 0; i < 32; i++)
				{
					int x = bpos.getX() + (int) (random.nextGaussian() * 2);
					int z = bpos.getZ() + (int) (random.nextGaussian() * 2);
					int y = bpos.getY() + random.nextInt(6);
					for (int j = 0; j < 6; j++)
					{
						BlockPos pos = new BlockPos(x, y - j, z);
						Block top = world.getBlockState(pos).getBlock();
						Block bottom = world.getBlockState(pos.down()).getBlock();
						if (top == Blocks.AIR && bottom instanceof BlockNetherrack)
						{
							world.setBlockState(pos, grass);
							if (random.nextInt(2) == 0)
								world.setBlockState(pos.down(), moss);
							break;
						}
					}
				}
				event.setResult(Result.ALLOW);
			}
		}
		else if (!world.isRemote && block == Blocks.RED_MUSHROOM)
		{
			BlockPos pos = event.getPos();
			if (world.getBlockState(pos.down()).getBlock() == BlocksRegistry.BLOCK_NETHER_MYCELIUM)
			{
				int size = 1 + random.nextInt(3);
				for (int y = 1; y <= size; y++)
					if (!world.isAirBlock(pos.up(y)))
					{
						size = y - 1;
						break;
					}
				if (size > 1)
				{
					event.setResult(Result.ALLOW);
					if (random.nextInt(4) == 0)
					{
						world.setBlockToAir(pos);
						IBlockState middle = BlocksRegistry
								.BLOCK_RED_LARGE_MUSHROOM
								.getDefaultState()
								.withProperty(BlockRedLargeMushroom.SHAPE, BlockRedLargeMushroom.EnumShape.MIDDLE);
						for (int y = 1; y < size; y++)
							world.setBlockState(pos.up(y), middle, 2);
						world.setBlockState(pos.up(size), BlocksRegistry
								.BLOCK_RED_LARGE_MUSHROOM
								.getDefaultState()
								.withProperty(BlockRedLargeMushroom.SHAPE, BlockRedLargeMushroom.EnumShape.TOP), 2);
						world.setBlockState(pos, BlocksRegistry
								.BLOCK_RED_LARGE_MUSHROOM
								.getDefaultState(), 2);
					}
				}
			}
		}
		else if (!world.isRemote && block == Blocks.BROWN_MUSHROOM)
		{
			BlockPos pos = event.getPos();
			if (world.getBlockState(pos.down()).getBlock() == BlocksRegistry.BLOCK_NETHER_MYCELIUM)
			{
				int size = 1 + random.nextInt(3);
				for (int y = 1; y <= size; y++)
					if (!world.isAirBlock(pos.up(y)))
					{
						size = y - 1;
						break;
					}
				if (size > 1)
				{
					event.setResult(Result.ALLOW);
					if (random.nextInt(4) == 0)
					{
						world.setBlockToAir(pos);
						IBlockState middle = BlocksRegistry
								.BLOCK_BROWN_LARGE_MUSHROOM
								.getDefaultState()
								.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.MIDDLE);
						world.setBlockState(pos, BlocksRegistry
								.BLOCK_BROWN_LARGE_MUSHROOM
								.getDefaultState(), 2);
						for (int y = 1; y < size; y++)
							world.setBlockState(pos.up(y), middle, 2);
						pos = pos.up(size);
						world.setBlockState(pos, BlocksRegistry
								.BLOCK_BROWN_LARGE_MUSHROOM
								.getDefaultState()
								.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.TOP), 2);
						if (world.isAirBlock(pos.north()))
							world.setBlockState(pos.north(), BlocksRegistry
									.BLOCK_BROWN_LARGE_MUSHROOM
									.getDefaultState()
									.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.SIDE_N), 2);
						if (world.isAirBlock(pos.south()))
							world.setBlockState(pos.south(), BlocksRegistry
									.BLOCK_BROWN_LARGE_MUSHROOM
									.getDefaultState()
									.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.SIDE_S), 2);
						if (world.isAirBlock(pos.east()))
							world.setBlockState(pos.east(), BlocksRegistry
									.BLOCK_BROWN_LARGE_MUSHROOM
									.getDefaultState()
									.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.SIDE_E), 2);
						if (world.isAirBlock(pos.west()))
							world.setBlockState(pos.west(), BlocksRegistry
									.BLOCK_BROWN_LARGE_MUSHROOM
									.getDefaultState()
									.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.SIDE_W), 2);
						if (world.isAirBlock(pos.north().east()))
							world.setBlockState(pos.north().east(), BlocksRegistry
									.BLOCK_BROWN_LARGE_MUSHROOM
									.getDefaultState()
									.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.CORNER_N), 2);
						if (world.isAirBlock(pos.north().west()))
							world.setBlockState(pos.north().west(), BlocksRegistry
									.BLOCK_BROWN_LARGE_MUSHROOM
									.getDefaultState()
									.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.CORNER_W), 2);
						if (world.isAirBlock(pos.south().east()))
							world.setBlockState(pos.south().east(), BlocksRegistry
									.BLOCK_BROWN_LARGE_MUSHROOM
									.getDefaultState()
									.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.CORNER_E), 2);
						if (world.isAirBlock(pos.south().west()))
							world.setBlockState(pos.south().west(), BlocksRegistry
									.BLOCK_BROWN_LARGE_MUSHROOM
									.getDefaultState()
									.withProperty(BlockBrownLargeMushroom.SHAPE, BlockBrownLargeMushroom.EnumShape.CORNER_S), 2);
					}
				}
			}
		}
	}
}
