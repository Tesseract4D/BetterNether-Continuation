package paulevs.betternether.structures.city;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class BoundingBox
{
	int x1, x2, z1, z2;
	
	public BoundingBox(int x1, int z1, int x2, int z2)
	{
		this.x1 = x1;
		this.x2 = x2;
		this.z1 = z1;
		this.z2 = z2;
	}
	
	public BoundingBox(BlockPos size, int offsetX, int offsetZ)
	{
		this.x1 = offsetX;
		this.x2 = x1 + size.getX();
		this.z1 = offsetZ;
		this.z2 = z1 + size.getZ();
	}

	public BoundingBox(BlockPos size) {
		this.x1 = 0;
		this.x2 = size.getX();
		this.z1 = 0;
		this.z2 = size.getZ();
	}

	public boolean isColliding(BoundingBox bb) {
		int bbX1 = bb.getMinX();
		int bbX2 = bb.getMaxX();
		int bbZ1 = bb.getMinZ();
		int bbZ2 = bb.getMaxZ();

		boolean colX = (bbX1 < x2) && (x1 < bbX2);
		boolean colZ = (bbZ1 < z2) && (z1 < bbZ2);
		return colX && colZ;
	}

	public void offset(BlockPos offset) {
		x1 += offset.getX();
		x2 += offset.getX();
		z1 += offset.getZ();
		z2 += offset.getZ();
	}

	public BoundingBox offsetNegative(BlockPos offset) {
		x1 -= offset.getX();
		x2 -= offset.getX();
		z1 -= offset.getZ();
		z2 -= offset.getZ();
		return new BoundingBox(x1, z1, x2, z2);
	}


	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(x1).append(" ").append(z1).append(" ").append(x2).append(" ").append(z2);
		return sb.toString();
	}

	public void rotate(Rotation rotation)
	{
		BlockPos start = new BlockPos(x1, 0, z1);
		BlockPos end = new BlockPos(x2, 0, z2);
		start = start.rotate(rotation);
		end = end.rotate(rotation);
		int nx1 = Math.min(start.getX(), end.getX());
		int nx2 = Math.max(start.getX(), end.getX());
		int nz1 = Math.min(start.getZ(), end.getZ());
		int nz2 = Math.max(start.getZ(), end.getZ());
		x1 = 0;
		z1 = 0;
		x2 = nx2 - nx1;
		z2 = nz2 - nz1;
	}

	public BlockPos getCenter()
	{
		return new BlockPos((x2 + x1) * 0.5, 0, (z2 + z1) * 0.5);
	}
	
	public int getSideX()
	{
		return x2 - x1;
	}
	
	public int getSideZ()
	{
		return z2 - z1;
	}
	
	public int getMinX()
	{
		return x1;
	}

	public int getMaxX()
	{
		return x2;
	}
	
	public int getMinZ()
	{
		return z1;
	}

	public int getMaxZ()
	{
		return z2;
	}
}
