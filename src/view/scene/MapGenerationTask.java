package view.scene;

import java.util.Random;

import map.Map;
import map.MapGenerator;

public class MapGenerationTask {

	private MapGenerator gen;
	private Thread thread;

	public void start(Map.Spec spec, Random rand) {
		gen = new MapGenerator(spec, rand);
		thread = new Thread(gen);
		thread.start();
	}
	
	public boolean hasStarted() {
		return gen != null;
	}
	
	public boolean hasFinished() {
		if (!hasStarted())
			return false;
		return !thread.isAlive();
	}
	
	public map.Map map() {
		return (gen != null) ? gen.map() : null;
	}
	
	public void clean() {
		gen = null;
		thread = null;
	}
}
