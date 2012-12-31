package pl.edu.pw.elka.mmarkiew.model;

import java.util.LinkedList;

/**
 * Class representing separated object from picture

 * @author Kajo
 */
public class Segment {

	/** Static initalized pixels binary map used to calculate perimeter */
	public static boolean[][] pixels = new boolean[1][1];

	/** Type of segment */
	public SegmentType type = SegmentType.UNDEFINED;
	
	/** Negative probability of assignment to that segment */
	public double typeProbability = 0;
	
	/** Id of given segment, used in segmentation */
	public int id;

	
	/** Width of whole image from segment is */
	public int imageWidth;
	
	/** Height of whole image from segment is */
	public int imageHeight;
	
	
	/** Width of segment */
	private int segmentWidth;
	
	/** Height of segment */
	private int segmentHeight;

	
	/** Minimum value of segment x coordinate */
	private int minX;

	/** Minimum value of segment y coordinate */
	private int minY;

	/** Maximum value of segment x coordinate */
	private int maxX;

	/** Maximum value of segment y coordinate */
	private int maxY;


	/** Minimum distance from segment edge to weight center of segment */
	private int rMin;

	/** Maximum distance from segment edge to weight center of segment */
	private int rMax;
	
	/** Maximum diameter of segmnet (distance from segment edge to another segment edge) */
	private int lMax;


	/** Area of segment */
	private long S;
	
	/** Segments perimeter */
	private long L;


	/** Array of counted mxys */
	private long moo[][] = new long[4][4];
	
	/** Array of calculated Mxys */
	private double Moo[][] = new double[4][4];

	
	/** Array of calculated geometry moments Wx */
	private double[] W = new double[10];
	
	/** Array of calculated invariants? of moments */
	private double[] M = new double[11];

	
	/** Array of binary pixels of segment */
	private boolean[][] binaryPixels;
	
	/** List of points assigned to segment */
	private LinkedList<Point> points = new LinkedList<>();

	/**
	 * C-tor
	 * 
	 * @param id Id of segment
	 * @param imageWidth Width of image from segment is
	 * @param imageHeight Height of image from segmnet is
	 */
	public Segment(final int id, final int imageWidth, final int imageHeight) {
		this.id = id;

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	/**
	 * Add point to segment
	 * 
	 * @param point Point to add
	 */
	public void addPoint(final Point point) {
		this.points.add(point);
	}

	/**
	 * Get list of points assigned to segment
	 * 
	 * @return List of points
	 */
	public LinkedList<Point> getPoints() {
		return this.points;
	}

	/**
	 * Calculate segment Moments and each values required to do that
	 */
	public void calculateMoments() {
		// It should be done first, so its additional run
//		calculateSizes();
		calculate_moo();
		calculate_Moo();
		calculateRsL();
		calculateW();
		calculateM();
	}

	/**
	 * Calculate minimum and maximum x,y coorinates
	 * area of segment and perimeter
	 */
	public void calculateSizes() {
		minX = imageWidth;
		minY = imageHeight;
		maxX = 0;
		maxY = 0;

		
		/*
		 * Area = amount of points assigned
		 */
		S = points.size();

		
		/*
		 * Find mins & maxes
		 */
		for (Point point : points) {
			minX = point.x < minX ? point.x : minX;
			maxX = point.x > maxX ? point.x : maxX;
			minY = point.y < minY ? point.y : minY;
			maxY = point.y > maxY ? point.y : maxY;
		}

		
		/*
		 * Calculate segment dimension
		 */
		this.segmentWidth = maxX - minX + 1;
		this.segmentHeight = maxY - minY + 1;


		/*
		 * Calculate perimeter
		 */
		L = 0;

		/*
		 * Create binary map of pixels
		 */
		binaryPixels = new boolean[this.segmentWidth][this.segmentHeight];
		for (int x = 0; x < this.segmentWidth; ++x)
			for (int y = 0; y < this.segmentHeight; ++y)
				binaryPixels[x][y] = false;

		for (Point point : points)
			binaryPixels[point.x - minX][point.y - minY] = true;

		// Find perimeter (by each adjacent pixel)
		for (int x = 0; x < this.segmentWidth; ++x)
			for (int y = 0; y < this.segmentHeight; ++y)
				if(binaryPixels[x][y])
					if (x == 0 || y == 0 || x == this.segmentWidth - 1 || y == this.segmentHeight - 1)
						++L;
					else
						for (int i = -1; i <= 1; ++i)
							for (int j = -1; j <= 1; ++j)
								if (!binaryPixels[x + i][y + j]) {
									++L;
									i = 2;
									break;
								}
	}

	/**
	 * Calculate mxys
	 */
	private void calculate_moo() {
		for (int i = 0; i < 4; ++i)
			for (int j = 0; j < 4; ++j)
				moo[i][j] = 0;

		for (Point point : points) {
			moo[0][0] += 1;
			moo[1][0] += point.x;
			moo[0][1] += point.y;
			moo[1][1] += point.x * point.y;
			moo[2][0] += point.x * point.x;
			moo[0][2] += point.y * point.y;
			moo[1][2] += point.x * point.y * point.y;
			moo[2][1] += point.x * point.x * point.y;
			moo[3][0] += point.x * point.x * point.x;
			moo[0][3] += point.y * point.y * point.y;
		}
	}

	/**
	 * Calculate Mxys
	 */
	private void calculate_Moo() {
		double iCenter = (double) moo[1][0] / moo[0][0];
		double jCenter = (double) moo[0][1] / moo[0][0];

		Moo[0][0] = moo[0][0];
		Moo[0][1] = moo[0][1] - (moo[0][1] / moo[0][0]) * moo[0][0];
		Moo[1][0] = moo[1][0] - (moo[1][0] / moo[0][0]) * moo[0][0];
		Moo[1][1] = moo[1][1] - (moo[1][0] * moo[0][1]) / moo[0][0];
		Moo[2][0] = moo[2][0] - (moo[1][0] * moo[1][0]) / moo[0][0];
		Moo[0][2] = moo[0][2] - (moo[0][1] * moo[0][1]) / moo[0][0];
		Moo[2][1] = moo[2][1] - 2 * moo[1][1] * iCenter - moo[2][0] * jCenter + 2 * moo[0][1] * iCenter * iCenter;
		Moo[1][2] = moo[1][2] - 2 * moo[1][1] * jCenter - moo[0][2] * iCenter + 2 * moo[1][0] * jCenter * jCenter;
		Moo[3][0] = moo[3][0] - 3 * moo[2][0] * iCenter + 2 * moo[1][0] * iCenter * iCenter;
		Moo[0][3] = moo[0][3] - 3 * moo[0][2] * jCenter + 2 * moo[0][1] * jCenter * jCenter;
	}

	/**
	 * Calculate usefull distances
	 * //TODO possible optimization: move to first calculateSizes
	 */
	private void calculateRsL() {
		int xWeight = xWeightCenter() - minX;
		int yWeight = yWeightCenter() - minY;

		rMin = Math.max(imageWidth, imageHeight);
		rMax = 1;

		/*
		 * Calculate rs
		 */
		for (int x = 0; x < this.segmentWidth; ++x)
			for (int y = 0; y < this.segmentHeight; ++y)
				if(binaryPixels[x][y])
					if (x == 0 || y == 0 || x == this.segmentWidth - 1 || y == this.segmentHeight - 1) {
						double dist = Math.sqrt(Math.pow(xWeight - x, 2) + Math.pow(yWeight - y, 2));
						
						rMin = (int) (dist < rMin ? dist : rMin);
						rMax = (int) (dist > rMax ? dist : rMax);
					} else
						for (int i = -1; i <= 1; ++i)
							for (int j = -1; j <= 1; ++j)
								if (!binaryPixels[x + i][y + j]) {
									double dist = Math.sqrt(Math.pow(xWeight - x, 2) + Math.pow(yWeight - y, 2));
									
									rMin = (int) (dist < rMin ? dist : rMin);
									rMax = (int) (dist > rMax ? dist : rMax);
	
									i = 2;
									break;
								}

		/*
		 * Calculate l
		 */
		lMax = (maxX - minX > maxY - minY ? maxX - minX : maxY - minY);
	}

	/**
	 * Calculate geometry moments (Wx)
	 */
	private void calculateW() {
		W[1] = 2 * Math.sqrt(S / Math.PI);
		W[2] = L / Math.PI;
		W[3] = L / (2 * Math.sqrt(Math.PI * S)) - 1;
		// 4,5,6 always different so useless
//		W[4] = 0;
//		W[5] = 0;
//		W[6] = 0;
		W[7] = (double) rMin / rMax;
		W[8] = (double) lMax / L;
		W[9] = (2 * Math.sqrt(Math.PI * S)) /  L;
	}

	/**
	 * Calculate invariants? moments 
	 */
	private void calculateM() {
		M[1] = (Moo[2][0] + Moo[0][2]) / Math.pow(moo[0][0], 2);
		M[2] = (Math.pow(Moo[2][0] - Moo[0][2], 2) + 4 * Moo[1][1] * Moo[1][1]) / Math.pow(moo[0][0], 4);
		M[3] = (Math.pow(Moo[3][0] - 3 * Moo[1][2], 2) + Math.pow(3 * Moo[2][1] - Moo[0][3], 2)) /
																								Math.pow(moo[0][0], 5);
		M[4] = (Math.pow(Moo[3][0] + Moo[1][2], 2) + Math.pow(Moo[2][1] + Moo[0][3], 2)) / Math.pow(moo[0][0], 5);
		M[5] = ((Moo[3][0] - 3 * Moo[1][2]) * (Moo[3][0] + Moo[1][2]) * (Math.pow(Moo[3][0] + Moo[1][2], 2) - 3 *
				Math.pow(Moo[2][1] + Moo[0][3], 2)) + (3 * Moo[2][1] - Moo[0][3]) * (Moo[2][1] + Moo[0][3])
				* (3 * Math.pow(Moo[3][0] + Moo[1][2], 2) - Math.pow(Moo[2][1] + Moo[0][3], 2))) /
																							Math.pow(moo[0][0], 10);
		M[6] = ((Moo[2][0] - Moo[0][2]) * (Math.pow(Moo[3][0] + Moo[1][2], 2) - Math.pow(Moo[2][1] + Moo[0][3], 2)) +
						4 * Moo[1][1] * (Moo[3][0] + Moo[1][2]) * (Moo[2][1] + Moo[0][3])) / Math.pow(moo[0][0], 7);
		M[7] = (Moo[2][0] * Moo[0][2] - Moo[1][1] * Moo[1][1]) / Math.pow(moo[0][0], 4);
		M[8] = (Moo[3][0] * Moo[1][2] + Moo[2][1] * Moo[0][3] - Moo[1][2] * Moo[1][2] - Moo[2][1] * Moo[2][1]) /
																								Math.pow(moo[0][0], 5);
		M[9] = (Moo[2][0] * (Moo[2][1] * Moo[0][3] - Moo[1][2] * Moo[1][2]) + Moo[0][2] * (Moo[0][3] * Moo[1][2] -
							Moo[2][1] * Moo[2][1]) - Moo[1][1] * (Moo[3][0] * Moo[0][3] - Moo[2][1] * Moo[1][2])) /
																								Math.pow(moo[0][0], 7);
		M[10] = (Math.pow(Moo[3][0] * Moo[0][3] - Moo[1][2] * Moo[2][1], 2) - 4 * (Moo[3][0] * Moo[1][2] - Moo[2][1] *
										Moo[2][1]) * (Moo[0][3] * Moo[2][1] - Moo[1][2])) / Math.pow(moo[0][0], 10);
	}

	/**
	 * Get invariant? moment by number
	 * 
	 * @param nr Number of moment from 1 to 9
	 * @return Calculated moment
	 */
	public double getMoment(final int nr) {
		if (nr < 1 || nr > 9)
			throw new IllegalArgumentException("Bad moment chosen.");

		return M[nr];
	}

	/**
	 * Get geometry moment by number
	 * 
	 * @param nr Number of moment from 1 to 9
	 * @return Calculated moment
	 */
	public double getWoment(final int nr) {
		if (nr < 1 || nr > 9)
			throw new IllegalArgumentException("Bad woment chosen.");

		return W[nr];
	}

	/**
	 * Get horizontal center of segment
	 * 
	 * @return x coordinate
	 */
	public int xCenter() {
		return (maxX + minX) / 2;
	}

	/**
	 * Get vertical center of segment
	 * 
	 * @return y cooridnate
	 */
	public int yCenter() {
		return (maxY + minY) / 2;
	}

	/**
	 * Get horizontal weight center of segment
	 * 
	 * @return x coordinate
	 */
	public int xWeightCenter() {
		return (int) (moo[1][0] / moo[0][0]);
	}

	/**
	 * Get vertical weight center of segment
	 * 
	 * @return y coordinate
	 */
	public int yWeightCenter() {
		return (int) (moo[0][1] / moo[0][0]);
	}

	/**
	 * Get minimum x coordinate of segment
	 * 
	 * @return Minimum x coordinate
	 */
	public int getMinX() {
		return this.minX;
	}

	/**
	 * Get minimum y coordinate of segment
	 * 
	 * @return Minimum x coordinate
	 */
	public int getMinY() {
		return this.minY;
	}

	/**
	 * Get maximum x coordinate of segment
	 * 
	 * @return Maximum x coordinate
	 */
	public int getMaxX() {
		return this.maxX;
	}

	/**
	 * Get maximum y coordinate of segment
	 * 
	 * @return Maximum x coordinate
	 */
	public int getMaxY() {
		return this.maxY;
	}

	/**
	 * Get area of segment
	 * 
	 * @return Segments area
	 */
	public long getS() {
		return this.S;
	}

	/**
	 * Get segment width
	 * 
	 * @return Segments width
	 */
	public int getSegmentWidth() {
		return this.segmentWidth;
	}
	
	/**
	 * Get segment height
	 * 
	 * @return Segments height
	 */
	public int getSegmentHeight() {
		return this.segmentHeight;
	}

	/**
	 * Just 4 debug and compare correctness of segmentation
	 * 
	 * @return Data of segment
	 */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();

		str.append("\tSegment " + id + ":\n");
		str.append("MinX:\t" + minX + "\n");
		str.append("MinY:\t" + minY + "\n");
		str.append("MaxX:\t" + maxX + "\n");
		str.append("MaxY:\t" + maxY + "\n");
		str.append("S:\t" + S + "\n");
		str.append("L:\t" + L + "\n");
		str.append("Rmin:\t" + rMin + "\n");
		str.append("Rmax:\t" + rMax + "\n");
		str.append("Lmax:\t" + lMax + "\n");

		for (int i = 0; i < 4; ++i)
			for (int j = 0; j < 4; ++j)
				str.append("m" + i + "" + j + ": " + moo[i][j] + "\n");

		for (int i = 0; i < 4; ++i)
			for (int j = 1; j < 4; ++j)
				str.append("M" + i + "" + j + ": " + Moo[i][j] + "\n");

		for (int i = 1; i < 10; ++i)
			str.append("W" + i + ": " + W[i] + "\n");
		
		for (int i = 1; i < 11; ++i)
			str.append("M" + i + ": " + M[i] + ( i < 10 ? "\n" : ""));
		
		str.append("\n\n");
		for (int y = 0; y < this.segmentHeight; ++y) {
			for (int x = 0; x < this.segmentWidth; ++x) {
				if (x == this.xWeightCenter() - minX && y == this.yWeightCenter() - minY)
					str.append("O");
				else if (x == this.xCenter() - minX && y == this.yCenter() - minY)
					str.append("X");
				else
					str.append(binaryPixels[x][y] ? "+" : " ");
			}
	
			str.append("\n");
		}
		
		return str.toString();
	}

}
