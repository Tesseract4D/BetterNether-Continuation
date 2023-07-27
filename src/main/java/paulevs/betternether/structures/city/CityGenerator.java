package paulevs.betternether.structures.city;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import paulevs.betternether.config.ConfigLoader;

public class CityGenerator
{
	private List<StructureCityBuilding> centers = new ArrayList<StructureCityBuilding>();
	private List<StructureCityBuilding> buildings = new ArrayList<StructureCityBuilding>();
	private List<StructureCityBuilding> roadEnds = new ArrayList<StructureCityBuilding>();
	private List<StructureCityBuilding> total = new ArrayList<StructureCityBuilding>();
	private List<BoundingBox> bounds = new ArrayList<BoundingBox>();
	private List<BlockPos> ends = new ArrayList<BlockPos>();
	private List<BlockPos> add = new ArrayList<BlockPos>();
	private List<BlockPos> rem = new ArrayList<BlockPos>();

	public CityGenerator()
	{
		for(String center : ConfigLoader.getCityCenters()) {
			addBuildingToList(center, -10, centers);
		}

		for(String building : ConfigLoader.getCityBuildings()) {
			addBuildingToList(building, buildings);
		}

		addBuildingToList("road_end_01", roadEnds);
		addBuildingToList("road_end_02", -2, roadEnds);

		total.addAll(centers);
		total.addAll(buildings);
		total.addAll(roadEnds);
	}

	private void addBuildingToList(String name, List<StructureCityBuilding> buildings)
	{
		addBuildingToList(name, 0, buildings);
	}

	private void addBuildingToList(String name, int offsetY, List<StructureCityBuilding> buildings)
	{
		StructureCityBuilding building = new StructureCityBuilding("city/" + name, offsetY);
		buildings.add(building);
		buildings.add(building.getRotated(Rotation.CLOCKWISE_90));
		buildings.add(building.getRotated(Rotation.CLOCKWISE_180));
		buildings.add(building.getRotated(Rotation.COUNTERCLOCKWISE_90));
	}

	private void placeCenterBuilding(BlockPos pos, StructureCityBuilding building, ArrayList<BuildingInfo> city)
	{
		BoundingBox bb = building.getBoundingBox().offset(pos);
		bounds.add(bb);
		city.add(new BuildingInfo(building, pos.add(0, building.getYOffset(), 0)));
		for (int i = 0; i < building.getEndsCount(); i++)
			ends.add(pos.add(building.getOffsettedPos(i).add(0, building.getYOffset(), 0)));
	}

	private void attachBuildings(Random random, ArrayList<BuildingInfo> city)
	{
		for (BlockPos pos : ends)
		{
			boolean generate = true;
			for (int n = 0; n < 8 && generate; n++)
			{
				int b = random.nextInt(buildings.size() >> 2) << 2;
				for (int r = 0; r < 4 && generate; r++)
				{
					StructureCityBuilding building = buildings.get(b | r);
					int index = random.nextInt(building.getEndsCount());
					BlockPos offset = building.getPos(index);
					BoundingBox bb = building.getBoundingBox().offset(pos).offsetNegative(offset);
					if (noCollisions(bb))
					{
						BlockPos npos = new BlockPos(bb.x1, pos.getY() - offset.getY() + building.getYOffset(), bb.z1);
						bounds.add(bb);
						rem.add(pos);
						for (int i = 0; i < building.getEndsCount(); i++)
							if (i != index)
								add.add(npos.add(building.getOffsettedPos(i)));
						city.add(new BuildingInfo(building, npos));
						generate = false;
					}
				}
			}
		}
		ends.removeAll(rem);
		ends.addAll(add);
		rem.clear();
		add.clear();
	}

	private void closeRoads(ArrayList<BuildingInfo> city)
	{
		for (BlockPos pos : ends)
		{
			for (int n = 0; n < roadEnds.size(); n++)
			{
				StructureCityBuilding building = roadEnds.get(n);
				BlockPos offset = building.getPos(0);
				BoundingBox bb = building.getBoundingBox().offset(pos).offsetNegative(offset);
				if (noCollisions(bb))
				{
					BlockPos npos = new BlockPos(bb.x1, pos.getY() - offset.getY() + building.getYOffset(), bb.z1);
					bounds.add(bb);
					city.add(new BuildingInfo(building, npos));
					break;
				}
			}
		}
		ends.clear();
		bounds.clear();
		rem.clear();
		add.clear();
	}

	public ArrayList<BuildingInfo> generate(BlockPos pos, Random random)
	{
		ArrayList<BuildingInfo> city = new ArrayList<BuildingInfo>();
		placeCenterBuilding(pos, centers.get(random.nextInt(centers.size())), city);
		for (int i = 0; i < 2 + random.nextInt(4); i++)
			attachBuildings(random, city);
		closeRoads(city);
		return city;
	}

	private boolean noCollisions(BoundingBox bb)
	{
		for (BoundingBox b : bounds)
			if (bb.isColliding(b))
				return false;
		return true;
	}

	public List<StructureCityBuilding> getBuildings()
	{
		return total;
	}
}
