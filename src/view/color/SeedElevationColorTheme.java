package view.color;

import java.util.List;

import types.Triple;

public class SeedElevationColorTheme extends NodeElevationColorTheme {

	public SeedElevationColorTheme(float elevMin, float elevRange,
			float surfaceElevRatio, boolean haveBeaches) {
		super(elevMin, elevRange, surfaceElevRatio, haveBeaches);
	}
	
	@Override
	public Triple<Float, Float, Float> getNodeColor(List<Float> vertices,
			int nodeIdx, int seedIdx, int lastNodeIdx) {
		int seedYCoordIdx = seedIdx * 3 + 1;
		return interpolateColor(vertices.get(seedYCoordIdx));
	}
}
