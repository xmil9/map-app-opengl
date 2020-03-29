package geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Algorithm for generating evenly distributed points.
// Implements Bridson's Algorithm:
// - Time: O(n)
// https://www.cs.ubc.ca/~rbridson/docs/bridson-siggraph07-poissondisk.pdf
public class PoissonDiscSampling {

	///////////////
	
	// Grid that divides the domain into cells each containing either the index of a
	// sample that lies within the cell or an empty marker. Allows to quickly lookup
	// if another sample is nearby.
	private static class BackgroundGrid {
		
		private static final double SQRT_TWO = 1.414213562373;
		private static final int EMPTY_CELL = -1;
		private final Rect2D domain;
		private final double minDist;
		private double cellSize;
		private int[][] grid;
		
		public BackgroundGrid(Rect2D domain, double minDist) {
			this.domain = domain;
			this.minDist = minDist;
			// Pick cell size so that when checking whether another point is within the
			// min distance of a given point only a small number of cells has to be
			// checked.
			// Using minDist/sqrt(2) means that the diagonal of a cell is minDist long:
			//   len(diagonal) = sqrt(cellSize^2 + cellSize^2)
			//            	   = sqrt((minDist/sqrt(2))^2 + (minDist/sqrt(2))^2)
			// 	               = sqrt(minDist^2 / 2 + minDist^2 / 2)
			//	               = sqrt(minDist^2)
			//	               = minDist
			// Therefore, for a given point only the following cells need to be checked:
			//   - cell of point itself
			//   - 2 cells in each direction (because cellSize < minDist < 2*cellSize)
			//   - 1 cell in each diagonal (because len(diagonal) == minDist) 
			this.cellSize = minDist / SQRT_TWO;
			this.grid = initGrid(makeGrid(domain, minDist, cellSize));
		}
		
		// Inserts the given index of a given sample into the grid.
		public void insert(Point2D sample, int sampleIdx) {
			int r = calcRow(sample.y);
			int c = calcCol(sample.x);
			grid[r][c] = sampleIdx;
		}
		
		// Checks whether another sample is within the minimal distance of a given
		// test point.
		public boolean haveSampleWithinMinDistance(Point2D test) {
			int testRow = calcRow(test.y);  
			int testCol = calcCol(test.x);

			int topMostRow = calcRow(test.y - minDist); 
			int bottomMostRow = calcRow(test.y + minDist);
			int leftMostCol = calcCol(test.x - minDist);
			int rightMostCol = calcCol(test.x + minDist);

			// Depending on where within its cell the test point is located we have to
			// check one or two cells into each direction, e.g. if the test point is
			// located in the top, left quadrant of its cell we have to check two cells
			// to the left and top of the test cell but only one cell to the right and
			// bottom. We have to do this for a strip of cells three cells thick
			// horizontally and vertically.
			
			// Proceed from the top most row to the bottom most.
			
			// If necessary, row of cells two cells above the test cell.
			if (topMostRow < testRow - 1) {
				for (int c = testCol - 1; c <= testCol + 1; ++c)
					if (isCellOccupied(topMostRow, c))
						return true;
			}
			// Center block of rows.
			for (int r = testRow - 1; r <= testRow + 1; ++r) {
				for (int c = leftMostCol; c <= rightMostCol; ++c)
					if (isCellOccupied(r, c))
						return true;
			}
			// If necessary, row of cells two cells below the test cell.
			if (bottomMostRow > testRow + 1) {
				for (int c = testCol - 1; c <= testCol + 1; ++c)
					if (isCellOccupied(bottomMostRow, c))
						return true;
			}
			
			return false;
		}
		
		// Creates a grid for a given domain.
		private static int[][] makeGrid(Rect2D domain, double minDist, double cellSize) {
			int numRows = (int) Math.ceil(domain.height() / cellSize);
			int numCols = (int) Math.ceil(domain.width() / cellSize);
			return new int[numRows][numCols];
		}
		
		// Initializes a given grid as empty.
		private static int[][] initGrid(int[][] grid) {
			int numRows = grid.length;
			if (numRows == 0)
				return grid;
			
			int numCols = grid[0].length;
			for (int r = 0; r < numRows; ++r)
				for (int c = 0; c < numCols; ++c)
					grid[r][c] = EMPTY_CELL;
			
			return grid;
		}
		
		// Calculates the row index of the grid cell that a given y-coordinate falls
		// on. 
		private int calcRow(double y) {
			return (int) Math.floor((y - domain.top()) / cellSize);
		}
		
		// Calculates the column index of the grid cell that a given x-coordinate
		// falls on. 
		private int calcCol(double x) {
			return (int) Math.floor((x - domain.left()) / cellSize);
		}
		
		// Checks if a cell at given coordinates is occupied.
		private boolean isCellOccupied(int r, int c) {
			if (r < 0 || r >= grid.length)
				return false;
			if (c < 0 || c >= grid[0].length)
				return false;
			return grid[r][c] != EMPTY_CELL;
		}
	}
	
	///////////////
	
	// Represents the ring-shaped area around a given point that candidate samples
	// are taken from.
	private static class Annulus {
		
		private final Ring2D ring;
		// Overlap of ring bounds and domain area.
		private final Rect2D bounds;
		private final Random rand;
		
		public Annulus(Point2D center, double innerRadius, double outerRadius,
				Rect2D domain, Random rand) {
			this.ring = new Ring2D(center, innerRadius, outerRadius);
			this.bounds = ring.bounds().intersect(domain);
			this.rand = rand;
		}
		
		public Point2D generatePointInRing() {
			Point2D pt = generatePointInBounds();
			while (!ring.isPointInRing(pt))
				pt = generatePointInBounds();
			return pt;
		}
		
		private Point2D generatePointInBounds() {
			double x = bounds.left() + rand.nextDouble() * bounds.width();
			double y = bounds.top() + rand.nextDouble() * bounds.height();
			return new Point2D(x, y);
		}
	}
	
	///////////////
	
	// Number of candidates that are generated when trying to find a new sample.
	public static final int NUM_CANDIDATES_DEFAULT = 30;
	private final Rect2D domain;
	// Min distance that samples are allowed to be from each other.
	private final double minDist;
	private final int numCandidates;
	// Max distance from seed sample that candidate samples are looked for. 
	private final double maxCandidateDist;
	private final Random rand;
	private List<Point2D> samples = new ArrayList<Point2D>();
	private List<Integer> active = new ArrayList<Integer>();
	private BackgroundGrid grid;
	
	public PoissonDiscSampling(Rect2D domain, double minDist, int numCandidatePoints,
			Random rand) {
		this.domain = domain;
		this.minDist = minDist;
		this.numCandidates = numCandidatePoints;
		this.maxCandidateDist = 2 * minDist;
		this.rand = rand;
		this.grid = new BackgroundGrid(domain, minDist);
	}
	
	// Generates samples by picking a random initial samples.
	public List<Point2D> generate() {
		return generate(generateSample());
	}
	
	// Generates samples with given initial sample.
	public List<Point2D> generate(Point2D initialSample) {
		storeSample(initialSample);

		while (!active.isEmpty()) {
			int seedIdx = chooseSeed();
			Point2D seedSample = samples.get(seedIdx);
			Point2D newSample = findNewSample(seedSample);
			if (newSample == null)
				deactivateSample(seedIdx);
			else
				storeSample(newSample);
		}
		
		return samples;
	}
	
	// Generates random sample.
	private Point2D generateSample() {
		double x = domain.left() + rand.nextDouble() * domain.width();
		double y = domain.top() + rand.nextDouble() * domain.height();
		return new Point2D(x, y);
	}
	
	// Abstracts the process of choosing the next seed sample to generate
	// candidates for. Returns index into sample array.
	private int chooseSeed() {
		return active.get(0);
	}
	
	// Stores a given sample in the internal data structures.
	private void storeSample(Point2D sample) {
		samples.add(sample);
		int sampleIdx = samples.size() - 1;
		active.add(sampleIdx);
		grid.insert(sample, sampleIdx);
	}
	
	// Marks a given sample as not active anymore
	private void deactivateSample(int sampleIdx) {
		active.remove(Integer.valueOf(sampleIdx));
	}
	
	// Finds a new sample for a given seed sample.
	// Returns null if none could be found.
	private Point2D findNewSample(Point2D seedSample) {
		Annulus annulus =
				new Annulus(seedSample, minDist, maxCandidateDist, domain, rand);
		
		for (int i = 0; i < numCandidates; ++i) {
			Point2D candidate = annulus.generatePointInRing();
			if (!grid.haveSampleWithinMinDistance(candidate))
				return candidate;
		}

		return null;
	}
}
