package paulevs.betternether.noise;

import net.minecraft.util.math.MathHelper;

/**
 * Cached OpenSimplex noise. 
 */
public class PregennedOpenSimplexNoise {
	private final int xArrSize, yArrSize;
	private final double[] noise;
	
	public PregennedOpenSimplexNoise(int xSize, int ySize, OpenSimplexNoise noiseGen) {
		xArrSize = (xSize >> 3) + 1;
		yArrSize = (ySize >> 3) + 1; // divide size of noise by 8 (sampling rate), add one for interpolating any remainder
		noise = new double[xArrSize * yArrSize]; // this performs better than a 2-dimensional array because java sucks
		for(int x = 0; x < xArrSize; x++) {
			for(int y = 0; y < yArrSize; y++) {
				put(x, y, noiseGen.eval(x, y));
			}
		}
	}
	
	private void put(int x, int y, double val) {
		noise[x * yArrSize + y] = val;
	}
	
	private double get(int x, int y) {
		return noise[x * yArrSize + y];
	}
	
	public double eval(int x, int y) {
		int lastX = x >> 3;
		int lastY = y >> 3;
		double distX = (x & 7) / 8D;
		double distY = (y & 7) / 8D;
		return lerp(get(lastX, lastY), distX * distY, get(lastX + 1, lastY + 1));
	}
	
	private static double lerp(double lower, double slide, double upper) {
		return lower + ((lower - upper) * slide);
	}
}
