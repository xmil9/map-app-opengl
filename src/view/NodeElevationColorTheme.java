package view;

import java.util.List;

import types.Triple;

public class NodeElevationColorTheme implements MapColorTheme {

	private final float elevMin;
	private final float elevRange;
	private final float surfaceElevRatio;
	private final boolean haveBeaches;
	
	public NodeElevationColorTheme(float elevMin, float elevRange,
			float surfaceElevRatio, boolean haveBeaches) {
		this.elevMin = elevMin;
		this.elevRange = elevRange;
		this.surfaceElevRatio = surfaceElevRatio;
		this.haveBeaches = haveBeaches;
	}
	
	@Override
	public Triple<Float, Float, Float> getNodeColor(List<Float> vertices,
			int nodeIdx, int seedIdx, int lastNodeIdx) {
		int nodeYCoordIdx = nodeIdx * 3 + 1;
		return interpolateColor(vertices.get(nodeYCoordIdx));
	}
	
	protected Triple<Float, Float, Float> interpolateColor(float elev) {
		float maxElev = elevMin + elevRange;
		float surfaceElev = elevMin + surfaceElevRatio * elevRange;
		float landRange = maxElev - surfaceElev;
		float beachElev = surfaceElev + 0.05f * landRange;
		float humidElev = surfaceElev + 0.33f * landRange;
		float aridElev = surfaceElev + 0.66f * landRange;
		float rockyElev = surfaceElev + 0.90f * landRange;
		
		float r = 0;
		float g = 0;
		float b = 0;
		if (elev < surfaceElev) {
			// Blue water.
			float minElev = elevMin;
			float elevSize = surfaceElev - minElev;
			r = 0;
			g = 0.5f * (elev - minElev) / elevSize;
			b = 1;
		} else if (elev < beachElev && haveBeaches) {
			// Yellow beaches.
			float minElev = surfaceElev;
			float elevSize = beachElev - minElev;
			r = 1f;
			g = 0.95f - 0.08f * (elev - minElev) / elevSize;
			b = 0.5f;
		} else if (elev < humidElev) {
			// Green vegetation.
			float minElev = haveBeaches ? beachElev : surfaceElev;
			float elevSize = humidElev - minElev;
			r = 0.03f;
			g = 0.34f +  0.3f * ((elev - minElev) / elevSize);
			b = 0;
		} else if (elev < aridElev) {
			// Brown grassland.
			float minElev = humidElev;
			float elevSize = aridElev - minElev;
			r = 0.86f;
			g = 0.8f - 0.3f * ((elev - minElev) / elevSize);
			b = 0;
		} else if (elev < rockyElev) {
			// Gray rocks.
			float minElev = aridElev;
			float elevSize = rockyElev - minElev;
			float gray = 0.75f - 0.5f * ((elev - minElev) / elevSize);
			r = gray;
			g = gray;
			b = gray;
		} else {
			// White snow.
			float minElev = rockyElev;
			float elevSize = maxElev - minElev;
			float gray = 0.85f + 0.15f * ((elev - minElev) / elevSize);
			r = gray;
			g = gray;
			b = gray;
		}
		
		return new Triple<Float, Float, Float>(r, g, b);
	}
}
