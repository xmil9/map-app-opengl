package map;

import java.util.Random;

public class MapGenerator implements Runnable {

	private final Map.Spec spec;
	private final Random rand;
	private Map map;
	
	public MapGenerator(Map.Spec spec, Random rand) {
		this.spec = spec;
		this.rand = rand;
	}
	
	public void run() {
		map = new map.Map(spec, rand);
		map.generate();
	}
	
	public Map map() {
		return map;
	}
}
