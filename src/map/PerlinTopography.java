package map;

import java.util.Random;

import geometry.Rect2D;
import math.MathUtil;
import math.PerlinNoise;

public class PerlinTopography implements TopographyGenerator {

	public static class Spec {
		public final Rect2D bounds;
		// Number of octaves used to aggregate Perlin noise value.
		// More octaves => Wider and wider areas are affected by values of
		// individual noise values of higher octave passes. Leads to zoomed in
		// appearance on features of the map (it's not an actual zooming/scaling
		// operation, though).
		public final int numOctaves;
		// Factor by which the amplitude of each octave pass is scaled with.
		// Larger persistence => Larger and smoother features.
		// Smaller persistence => Smaller and choppier features.
		public final double persistence;
		
		public Spec(Rect2D bounds, int numOctaves, double persistence) {
			this.bounds = bounds;
			this.numOctaves = numOctaves;
			this.persistence = persistence;
		}
	}
	
	///////////////
	
	private final Spec spec;
	private final int left;
	private final int top;
	private final int width;
	private final int height;
	private final Random rand;
	
	public PerlinTopography(Spec spec, Random rand) {
		this.spec = spec;
		this.left = (int) (spec.bounds.left());
		this.top = (int) (spec.bounds.top());
		this.width = (int) (spec.bounds.right() - left) + 1;
		this.height = (int) (spec.bounds.bottom() - top) + 1;
		this.rand = rand;
	}
	
	@Override
	public void generate(Map.Representation rep) {
		PerlinNoise perlinGen = new PerlinNoise(width, height, rand);
		
		for (int i = 0; i < rep.countNodes(); ++i) {
			MapNode node = rep.node(i);
			double noise = perlinGen.calcOctaveNoise(node.pos, spec.numOctaves,
					spec.persistence);
			node.setElevation(scaleElevation(noise));
		}
		
		for (int i = 0; i < rep.countTiles(); ++i) {
			MapTile tile = rep.tile(i);
			double noise = perlinGen.calcOctaveNoise(tile.seed, spec.numOctaves,
					spec.persistence);
			tile.setElevation(scaleElevation(noise));
		}
	}
	
	// Scales elevation values because Perlin noise values calculated with multiple
	// octaves tend to be pretty flat.
	private static double scaleElevation(double elev) {
		return MathUtil.clampToRange(stretch(elev), -1.0, 1.0);
	}

	// Stretches a given value depending on it's range.
	private static double stretch(double t) {
		double abs = Math.abs(t);
		if (abs < .3)
			return 4 * t;
		else if (abs < .5)
			return 3 * t;
		return t;
	}
}
