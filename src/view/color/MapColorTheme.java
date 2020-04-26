package view.color;

import java.util.List;

import types.Triple;

public interface MapColorTheme {
	public Triple<Float, Float, Float> getNodeColor(List<Float> vertices, int nodeIdx,
			int seedIdx, int lastNodeIdx);
}
