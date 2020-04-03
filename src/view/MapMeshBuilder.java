package view;

public class MapMeshBuilder {

	private final map.Map map;
	
	public MapMeshBuilder(map.Map map) {
		this.map = map;
	}
	
	public Mesh build() {
		
		return new Mesh(null, null, null, null, null);
	}
}
