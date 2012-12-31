package pl.edu.pw.elka.mmarkiew.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Class responsible for image segmentation
 * 
 * @author Mikolaj Markiewicz
 */
public class Segmentator {

	/** Width of image */
	private int width;
	
	/** Height of image */
	private int height;
	
	/** Binary image as array */
	private boolean[][] binaryPixels;

	/** Array of segments ids to which point is assigned */
	private Point[][] pointsAssignment;

	/** Map of segments with id as key and segment as value */
	private HashMap<Integer, Segment> segments = new HashMap<>();

	/**
	 * C-tor
	 * 
	 * @param binaryPixels Binary pixels array to segmentate from
	 */
	public Segmentator(final boolean[][] binaryPixels) {
		this.width = binaryPixels.length;
		this.height = binaryPixels[0].length;

		this.binaryPixels = binaryPixels;
		this.pointsAssignment = new Point[width][height];

		/*
		 * Set them all not assigned
		 */
		for (int x = 0; x < width; ++x)
			for (int y = 0; y < height; ++y)
				pointsAssignment[x][y] = new Point(x, y, -1);
	}

	/**
	 * Get list of segments found
	 * 
	 * @return List of segments
	 */
	public ArrayList<Segment> getSegments() {
		return new ArrayList<Segment>(this.segments.values());
	}

	/**
	 * Do proper segmentation
	 */
	public void segmentation() {
		/** Segment id generator */
		int segmentId = 0;

		/*
		 * Go through image
		 */
		for (int y = 0; y < height; ++y)
			for (int x = 0; x < width; ++x) {
//				if (binaryPixels[x][y]) { // TODO uncomment that condition if only 'white' are considered
					/*
					 * Create unassigned point
					 */
					Point tmpPoint = new Point(x, y, -1);

					/*
					 * If first line, specific conditions
					 */
					if (y == 0) {
						/*
						 * If first pixel or different value as left one
						 * generate new segment and add considered point to it
						 */
						if (x == 0 || binaryPixels[x][y] != binaryPixels[x - 1][y]) {
							Segment s = new Segment(segmentId++, width, height);
							tmpPoint.segmentId = segmentId - 1;
							s.addPoint(tmpPoint);
							segments.put(segmentId - 1, s);
							pointsAssignment[x][y].segmentId = segmentId - 1;
						}
						/*
						 * Else add considered point to segment to which left point is assigned
						 */
						else {
							tmpPoint.segmentId = pointsAssignment[x - 1][y].segmentId;
							segments.get(tmpPoint.segmentId).addPoint(tmpPoint);
							pointsAssignment[x][y].segmentId = tmpPoint.segmentId;
						}
					}
					/*
					 * Else if it's not first line
					 */
					else {
						/*
						 * If it's first column consider only with top pixels
						 */
						if (x == 0) {
							/*
							 * If different value as top one
							 * generate new segment
							 */
							if (binaryPixels[x][y] != binaryPixels[x][y - 1]) {
								Segment s = new Segment(segmentId++, width, height);
								tmpPoint.segmentId = segmentId - 1;
								s.addPoint(tmpPoint);
								segments.put(segmentId - 1, s);
								pointsAssignment[x][y].segmentId = segmentId - 1;
							}
							/*
							 * Else add point to 'top segment'
							 */
							else {
								tmpPoint.segmentId = pointsAssignment[x][y - 1].segmentId;
								segments.get(tmpPoint.segmentId).addPoint(tmpPoint);
								pointsAssignment[x][y].segmentId = tmpPoint.segmentId;
							}
						}
						/*
						 * Else if it's not first pixel in the row do more..
						 */
						else {
							/*
							 * If considered pixel value is same as left
							 * consider also top one
							 */
							if (binaryPixels[x][y] == binaryPixels[x - 1][y]) {
								/*
								 * First set as left one
								 */
								tmpPoint.segmentId = pointsAssignment[x - 1][y].segmentId;
								pointsAssignment[x][y].segmentId = tmpPoint.segmentId;
								
								/*
								 * Next check top
								 * If same value as top one and left one and top one id's aren't the same
								 * put points from segment with higer id into segment with lower id - less moving
								 */
								if (binaryPixels[x][y] == binaryPixels[x][y - 1] &&
											pointsAssignment[x][y].segmentId != pointsAssignment[x][y - 1].segmentId) {
									/*
									 * Get lower id to less moving things
									 */
									int segmentIdToAddToIt = Math.min(pointsAssignment[x][y - 1].segmentId, pointsAssignment[x - 1][y].segmentId);
		                            int segmentIdToRemove = Math.max(pointsAssignment[x][y - 1].segmentId, pointsAssignment[x - 1][y].segmentId);

									tmpPoint.segmentId = segmentIdToAddToIt;
									pointsAssignment[x][y].segmentId = tmpPoint.segmentId;
									Segment addItemsTo = segments.get(segmentIdToAddToIt);
									addItemsTo.addPoint(tmpPoint);

									/*
									 * Go through points from higher segment and move them into another
									 */
									Segment toBeRemoved = segments.get(segmentIdToRemove);
									LinkedList<Point> pointses = toBeRemoved.getPoints();
									for (int i = 0; i < pointses.size(); ++i) {
										Point ic = pointses.get(i);
										ic.segmentId = segmentIdToAddToIt;
										pointsAssignment[ic.x][ic.y].segmentId = segmentIdToAddToIt;
										addItemsTo.addPoint(ic);
									}
									
									/*
									 * Remove unused segment
									 */
									segments.remove(segmentIdToRemove);
								}
								/*
								 * Else add point to 'left segment'
								 */
								else {
									segments.get(tmpPoint.segmentId).addPoint(tmpPoint);
								}
							}
							/*
							 * Else if different, consider only with top
							 */
							else {
								/*
								 * If same as top, add to top one segment
								 */
								if (binaryPixels[x][y] == binaryPixels[x][y - 1]) {
									tmpPoint.segmentId = pointsAssignment[x][y - 1].segmentId;
									segments.get(tmpPoint.segmentId).addPoint(tmpPoint);
									pointsAssignment[x][y].segmentId = tmpPoint.segmentId;
								}
								/*
								 * Else create new one segment
								 */
								else {
									Segment s = new Segment(segmentId++, width, height);
									tmpPoint.segmentId = segmentId - 1;
									s.addPoint(tmpPoint);
									segments.put(segmentId - 1, s);
									pointsAssignment[x][y].segmentId = segmentId - 1;
								}
							}
						}
//					}

					pointsAssignment[x][y] = tmpPoint;
				}
			}

	}
	
}
