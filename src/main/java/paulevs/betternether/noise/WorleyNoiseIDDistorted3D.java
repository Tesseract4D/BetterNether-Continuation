package paulevs.betternether.noise;

public class WorleyNoiseIDDistorted3D {
	private WorleyNoiseID3D idNoise;
	private WorleyNoiseOctaved noise;
	private double cachedX;
	private double cachedY;
	private double cachedZ;
	private double cachedNoiseX;
	private double cachedNoiseY;
	private double cachedNoiseZ;

	public WorleyNoiseIDDistorted3D(long seed, int maxID) {
		idNoise = new WorleyNoiseID3D(seed, maxID);
		noise = new WorleyNoiseOctaved(seed, 3);
		cachedX = Double.NaN;
		cachedY = Double.NaN;
		cachedZ = Double.NaN;
		cachedNoiseX = Double.NaN;
		cachedNoiseY = Double.NaN;
		cachedNoiseZ = Double.NaN;
	}

	public int GetValue(double x, double y, double z) {
		if (x != cachedX) {
			cachedNoiseY = noise.GetValue(x * 0.5, -cachedZ * 0.5);
			cachedNoiseZ = noise.GetValue(-x * 0.5, -cachedY * 0.5);
			cachedX = x;
		}
		if (y != cachedY) {
			cachedNoiseX = noise.GetValue(cachedY * 0.5, -z * 0.5);
			cachedNoiseZ = noise.GetValue(-cachedX * 0.5, -y * 0.5);
			cachedY = y;
		}
		if (z != cachedZ) {
			cachedNoiseX = noise.GetValue(cachedZ * 0.5, -y * 0.5);
			cachedNoiseY = noise.GetValue(x * 0.5, -cachedX * 0.5);
			cachedZ = z;
		}
		double nx = x + cachedNoiseX;
		double ny = y + cachedNoiseY;
		double nz = z + cachedNoiseZ;
		return idNoise.getBiome(nx, ny, nz);
	}
}