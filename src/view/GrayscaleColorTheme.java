package view;

import java.util.List;
import java.util.Random;

import types.Triple;

public class GrayscaleColorTheme implements MapColorTheme {

	private final Random rand;
	
	public GrayscaleColorTheme(Random rand) {
		this.rand = rand;
	}
	
	@Override
	public Triple<Float, Float, Float> getNodeColor(List<Float> vertices,
			int nodeIdx, int seedIdx, int lastNodeIdx) {
		float gray = 0.5f + 0.5f * rand.nextFloat();
		return new Triple<Float, Float, Float>(gray, gray, gray);
	}

}
