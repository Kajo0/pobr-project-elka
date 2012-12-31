package pl.edu.pw.elka.mmarkiew.model;

import java.util.Iterator;
import java.util.LinkedList;

public class Segment {

	public static boolean[][] pixels = new boolean[1][1];

	public SegmentType type = SegmentType.UNDEFINED;
	public double typeProbability = 0;
	public int id;
	
	public int imageWidth;
	public int imageHeight;
	
	private int segmentWidth;
	private int segmentHeight;

	private int minX;
	private int minY;
	private int maxX;
	private int maxY;

	private int rMin;
	private int rMax;
	private int lMax;

	private long S;
	private long L;

	private long moo[][] = new long[4][4];
	private double Moo[][] = new double[4][4];
	
	private double[] W = new double[10];
	private double[] M = new double[11];
	
//////	private boolean[][] pixels;
	private boolean[][] binaryPixels;
	private LinkedList<Point> points = new LinkedList<>();

	public Segment(final int id, final int imageWidth, final int imageHeight) {
		this.id = id;
		
//////		this.pixels = new boolean[imageWidth][imageHeight];
//////
//////		for (int x = 0; x < imageWidth; ++x)
//////			for (int y = 0; y < imageHeight; ++y)
//////				this.pixels[x][y] = false;

		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
	}

	public void addPoint(Point point) {
		this.points.add(point);
	}
	
	public LinkedList<Point> getPoints() {
		return this.points;
	}
	
	public void lightPixel(final int x, final int y) {
		this.pixels[x][y] = true;
	}
	
	public void calculateMoments() {
//		calculateMinMax();
//		calculateS();
//		calculateL();
		calculateSizes();
		calculate_moo();
		calculate_Moo();
		calculateRsL();
		calculateW();
		calculateM();
	}
	
	public void calculateSizes() {
		minX = imageWidth;
		minY = imageHeight;
		maxX = 0;
		maxY = 0;

		
		S = points.size();

		
		for (Point point : points) {
			minX = point.x < minX ? point.x : minX;
			maxX = point.x > maxX ? point.x : maxX;
			minY = point.y < minY ? point.y : minY;
			maxY = point.y > maxY ? point.y : maxY;
		}

		this.segmentWidth = maxX - minX + 1;
		this.segmentHeight = maxY - minY + 1;


		L = 0;

		binaryPixels = new boolean[this.segmentWidth][this.segmentHeight];
		for (int x = 0; x < this.segmentWidth; ++x)
			for (int y = 0; y < this.segmentHeight; ++y)
				binaryPixels[x][y] = false;

		for (Point point : points)
			binaryPixels[point.x - minX][point.y - minY] = true;

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

//		for (int x = 0; x < imageWidth; ++x)
//			for (int y = 0; y < imageHeight; ++y)
//				if(pixels[x][y]) {
//					minX = x < minX ? x : minX;
//					maxX = x > maxX ? x : maxX;
//					minY = y < minY ? y : minY;
//					maxY = y > maxY ? y : maxY;
//					
//					++S;
//
//					if (x == 0 || y == 0 || x == imageWidth -1 || y == imageHeight - 1)
//						++L;
//					else
//						for (int i = -1; i <= 1; ++i)
//							for (int j = -1; j <= 1; ++j)
//								if (!pixels[x + i][y + j]) {
//									++L;
//									i = 2;
//									break;
//								}
//				}
	}

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
//		for (int x = 0; x < imageWidth; ++x)
//			for (int y = 0; y < imageHeight; ++y)
//				if (pixels[x][y]) {
//					moo[0][0] += 1;
//					moo[1][0] += x;
//					moo[0][1] += y;
//					moo[1][1] += x * y;
//					moo[2][0] += x * x;
//					moo[0][2] += y * y;
//					moo[1][2] += x * y * y;
//					moo[2][1] += x * x * y;
//					moo[3][0] += x * x * x;
//					moo[0][3] += y * y * y;
//				}
	}

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

	private void calculateRsL() {
		int xWeight = xWeightCenter() - minX;
		int yWeight = yWeightCenter() - minY;

		rMin = Math.max(imageWidth, imageHeight);
		rMax = 1;

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

//		for (int x = 0; x < imageWidth; ++x)
//			for (int y = 0; y < imageHeight; ++y)
//				if (pixels[x][y])
//					if (x == 0 || y == 0 || x == imageWidth -1 || y == imageHeight - 1) {
//						double dist = Math.sqrt(Math.pow(xWeight - x, 2) + Math.pow(yWeight - y, 2));
//						
//						rMin = (int) (dist < rMin ? dist : rMin);
//						rMax = (int) (dist > rMax ? dist : rMax);
//					} else
//						for (int i = -1; i <= 1; ++i)
//							for (int j = -1; j <= 1; ++j)
//								if (!pixels[x + i][y + j]) {
//									double dist = Math.sqrt(Math.pow(xWeight - x, 2) + Math.pow(yWeight - y, 2));
//									
//									rMin = (int) (dist < rMin ? dist : rMin);
//									rMax = (int) (dist > rMax ? dist : rMax);
//
//									i = 2;
//									break;
//								}

		lMax = (maxX - minX > maxY - minY ? maxX - minX : maxY - minY);
	}

	private void calculateW() {
		W[1] = 2 * Math.sqrt(S / Math.PI);
		W[2] = L / Math.PI;
		W[3] = L / (2 * Math.sqrt(Math.PI * S)) - 1;
//		W[4] = 0;
//		W[5] = 0;
//		W[6] = 0;
		W[7] = (double) rMin / rMax;
		W[8] = (double) lMax / L;
		W[9] = (2 * Math.sqrt(Math.PI * S)) /  L;
	}

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

	public boolean[][] getPixels() {
		return this.pixels;
	}

	public double getMoment(final int nr) {
		if (nr < 1 || nr > 9)
			throw new IllegalArgumentException("Bad moment chosen.");

		return M[nr];
	}
	
	public double getWoment(final int nr) {
		if (nr < 1 || nr > 9)
			throw new IllegalArgumentException("Bad woment chosen.");

		return W[nr];
	}

	public int xCenter() {
		return (maxX + minX) / 2;
	}

	public int yCenter() {
		return (maxY + minY) / 2;
	}

	public int xWeightCenter() {
		return (int) (moo[1][0] / moo[0][0]);
	}

	public int yWeightCenter() {
		return (int) (moo[0][1] / moo[0][0]);
	}

	public int getMinX() {
		return this.minX;
	}

	public int getMinY() {
		return this.minY;
	}

	public int getMaxX() {
		return this.maxX;
	}

	public int getMaxY() {
		return this.maxY;
	}

	public int getId() {
		return this.id;
	}

	public long getS() {
		return this.S;
	}
	
	public int getSegmentWidth() {
		return this.segmentWidth;
	}
	
	public int getSegmentHeight() {
		return this.segmentHeight;
	}

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
		
//		for (int y = 0; y < pixels[0].length; ++y) {
//			for (int x = 0; x < pixels.length; ++x)
//				str.append(pixels[x][y] ? "+" : " ");
//
//			str.append("\n");
//		}
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
