package map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import math.MathUtil;

// Generates the surface profile of a map. 
public class ContinentBasedTopography implements TopographyGenerator {

	public static class Spec {
		public final double landRatio;
		public final int numContinents;
		public final ContinentGenerator continentGen;
		
		public Spec(Random rand, ContinentGenerator continentGen) {
			this(MathUtil.randGaussian(rand, 0,  1),
					1 + rand.nextInt(20),
					continentGen);
		}
		
		public Spec(double landRatio, int numContinents,
				ContinentGenerator continentGen) {
			this.landRatio = landRatio;
			this.numContinents = numContinents;
			this.continentGen = continentGen;
		}
	}

	public interface ContinentGenerator {
		public void setMap(Map.Representation rep);
		public void generate(Continent continent);
	}

	///////////////
	
	private Map.Representation rep;
	private final Spec spec;
	private final Random rand;
	private final ContinentGenerator continentGen;
	private List<Continent> continents;

	public ContinentBasedTopography(Spec spec, Random rand) {
		this.spec = spec;
		this.rand = rand;
		this.continentGen = spec.continentGen;
	}
	
	@Override
	public void generate(Map.Representation rep) {
		this.rep = rep;
		continentGen.setMap(rep);
		
		generateContinents();
	}
	
	private void generateContinents() {
		initContinents();
		for (var continent : continents)
			generateContinent(continent);
	}
	
	// Initializes the internal data structures according to the given spec.
	private void initContinents() {
		continents = new ArrayList<Continent>(spec.numContinents);
		
		final int totalLandNodes = (int) (rep.countNodes() * spec.landRatio);
		int landNodesRemaining = totalLandNodes;
		
		for (int i = 0; i < spec.numContinents - 1; ++i) {
			int continentsRemaining = spec.numContinents - i;
			int continentSize = (landNodesRemaining > continentsRemaining) ?
					rand.nextInt(landNodesRemaining - continentsRemaining) : 0;
			landNodesRemaining -= continentSize;
			continents.add(new Continent(continentSize));
		}
		
		// Last continent.
		continents.add(new Continent(landNodesRemaining));
	}
	
	private void generateContinent(Continent continent) {
		spec.continentGen.generate(continent);
	}
}
