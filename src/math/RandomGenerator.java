package math;

import java.util.Random;

public class RandomGenerator {

	private long seed;
	private Random rand;
	
	public RandomGenerator(long seed) {
		this.seed = seed;
		this.rand = new Random(seed);
	}

	public RandomGenerator() {
		this(new Random().nextLong());
	}
	
	public long seed() {
		return seed;
	}

	public Random rand() {
		return rand;
	}
	
	public float nextFloat() {
		return rand.nextFloat();
	}
	
	public double nextDouble() {
		return rand.nextDouble();
	}
	
	public int nextDouble(int maxExclusive) {
		return rand.nextInt(maxExclusive);
	}
	
	public void reset() {
		seed = new Random().nextLong();
		rand = new Random(seed);
	}
}
